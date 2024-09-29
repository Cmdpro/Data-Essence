package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.api.databank.Minigame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MinesweeperMinigame extends Minigame {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_bank_minigames.png");
    public MinesweeperMinigame(int bombCount, int size) {
        this.bombCount = bombCount;
        this.size = size;
        randomizeBombs(bombCount);
    }
    public int bombCount;
    public int size;
    public void randomizeBombs(int bombCount) {
        List<Vector2i> noBombPos = new ArrayList<>();

        noBombPos.add(new Vector2i(6, 6));
        noBombPos.add(new Vector2i(6, 7));
        noBombPos.add(new Vector2i(6, 8));
        noBombPos.add(new Vector2i(7, 6));
        noBombPos.add(new Vector2i(7, 7));
        noBombPos.add(new Vector2i(7, 8));
        noBombPos.add(new Vector2i(8, 6));
        noBombPos.add(new Vector2i(8, 7));
        noBombPos.add(new Vector2i(8, 8));

        List<Vector2i> bombs = new ArrayList<>();
        for (int i = 0; i < bombCount; i++) {
            Vector2i pos = new Vector2i(RandomUtils.nextInt(7-size, 7+size), RandomUtils.nextInt(7-size, 7+size));
            while (bombs.contains(pos) || noBombPos.contains(pos)) {
                pos = new Vector2i(RandomUtils.nextInt(7-size, 7+size), RandomUtils.nextInt(7-size, 7+size));
            }
            bombs.add(pos);
        }
        setBombs(bombs);
        revealSegment(new Vector2i(7, 7), null);
    }
    @Override
    public boolean isFinished() {
        if (tiles == null || bombs == null) {
            return false;
        }
        boolean finished = true;
        for (Tile i : tiles.values()) {
            if (!i.cleared && !i.isBomb) {
                finished = false;
                break;
            }
        }
        return finished;
    }
    public List<Vector2i> bombs;
    public HashMap<Vector2i, Tile> tiles;
    public void setBombs(List<Vector2i> bombs) {
        this.bombs = bombs;
        this.tiles = new HashMap<>();
        for (int i = 0; i < 150/10; i++) {
            for (int o = 0; o < 150 / 10; o++) {
                Tile tile = new Tile();
                Vector2i pos = new Vector2i(i, o);
                tile.pos = pos;
                if (i < 7-size || o < 7-size || i > 7+size || o > 7+size) {
                    tile.nearbyBombs = 0;
                    tile.cleared = true;
                    tile.isBomb = false;
                } else {
                    tile.isBomb = bombs.contains(pos);
                    int nearbyBombs = 0;
                    for (int i2 = -1; i2 <= 1; i2++) {
                        for (int o2 = -1; o2 <= 1; o2++) {
                            Vector2i pos2 = new Vector2i(i + i2, o + o2);
                            if (bombs.contains(pos2)) {
                                nearbyBombs++;
                            }
                        }
                    }
                    tile.nearbyBombs = nearbyBombs;
                }
                tiles.put(pos, tile);
            }
        }
    }
    public void revealSegment(Vector2i start, Tile last) {
        if (last != null && last.nearbyBombs > 0) {
            return;
        }
        Tile tile = getTile(start);
        if (tile == null || tile.cleared || tile.isBomb) {
            return;
        }
        tile.cleared = true;
        for (int i = -1; i <= 1; i++) {
            for (int o = -1; o <= 1; o++) {
                if (i != 0 || o != 0) {
                    revealSegment(new Vector2i(start.x+i, start.y+o), tile);
                }
            }
        }
    }
    public Tile getTile(Vector2i pos) {
        return tiles.get(pos);
    }
    public int gameOver = -1;
    @Override
    public void render(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y) {
        for (Tile i : tiles.values()) {
            if (!i.cleared) {
                if (pMouseX >= x + (i.pos.x * 10) && pMouseY >= y + (i.pos.y * 10) && pMouseX <= x + (i.pos.x * 10) + 9 && pMouseY <= y + (i.pos.y * 10) + 9) {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 11, i.isMarked ? 11 : 0, 10, 10);
                } else {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 0, i.isMarked ? 11 : 0, 10, 10);
                }
            } else {
                if (i.isBomb) {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 33, 0, 10, 10);
                } else {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 22, 0, 10, 10);
                    if (i.nearbyBombs > 0) {
                        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, String.valueOf(i.nearbyBombs), x + (i.pos.x * 10) + 5, y + (i.pos.y * 10) + 1, 0xFFFFFFFF);
                    }
                }
            }
        }
        if (gameOver >= 0) {
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0, 0, 399);
            pGuiGraphics.fill(x, y, x+150, y+(int)(75f*((float)gameOver/50f)), 0xFF000000);
            pGuiGraphics.fill(x, y+150-(int)(75f*((float)gameOver/50f)), x+150, y+150, 0xFF000000);
            pGuiGraphics.pose().popPose();
        }
    }

    @Override
    public void tick() {
        if (gameOver >= 0) {
            gameOver++;
            if (gameOver >= 50) {
                gameOver = -1;
                randomizeBombs(bombCount);
            }
        }
    }

    @Override
    public void mouseClicked(double pMouseX, double pMouseY, int pButton) {
        super.mouseClicked(pMouseX, pMouseY, pButton);
        if (gameOver == -1) {
            for (Tile i : tiles.values()) {
                if (!i.cleared) {
                    if (pMouseX >= (i.pos.x * 10) && pMouseY >= (i.pos.y * 10) && pMouseX <= (i.pos.x * 10) + 9 && pMouseY <= (i.pos.y * 10) + 9) {
                        if (pButton == 0) {
                            i.cleared = true;
                            i.isMarked = false;
                            Client.breakTile();
                            if (i.isBomb) {
                                Client.explode();
                                gameOver = 0;
                            }
                        } else {
                            if (!i.isMarked) {
                                Client.placeFlag();
                            } else {
                                Client.removeFlag();
                            }
                            i.isMarked = !i.isMarked;
                        }
                        break;
                    }
                }
            }
        }
    }
    public class Tile {
        public boolean cleared;
        public Vector2i pos;
        public int nearbyBombs;
        public boolean isBomb;
        public boolean isMarked;
    }
    public class Client {
        public static void placeFlag() {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOOL_PLACE, 1f, 1f));
        }
        public static void removeFlag() {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOOL_BREAK, 1f, 1f));
        }
        public static void breakTile() {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.STONE_BREAK, 1f, 1f));
        }
        public static void explode() {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.GENERIC_EXPLODE.value(), 1f, 1f));
        }
    }
}
