package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.screen.databank.Minigame;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WireMinigame extends Minigame {
    public static final ResourceLocation TEXTURE = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/data_bank_minigames.png");
    public WireMinigame(int bombCount, int size) {

    }
    @Override
    public boolean isFinished() {
        boolean finished = true;
        for (Tile i : tiles.values()) {
            if (i.type == 1) {
                if (!isConnectedToEnd(i)) {
                    finished = false;
                    break;
                }
            }
        }
        return finished;
    }
    public boolean isConnectedToEnd(Tile tile) {
        if (tile.type == 1 || tile.type == 3) {
            Tile tile2 = getTile(tile.connectedTo);
            if (tile2 != null) {
                if (tile2.type == 2) {
                    return true;
                } else {
                    return isConnectedToEnd(tile2);
                }
            }
        } else {
            return true;
        }
        return false;
    }
    public HashMap<Vector2i, Tile> tiles;
    public Tile getTile(Vector2i pos) {
        return tiles.get(pos);
    }
    @Override
    public void render(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y) {
        for (Tile i : tiles.values()) {
            if (i.type == 0) {
                if (pMouseX >= x + (i.pos.x * 10) && pMouseY >= y + (i.pos.y * 10) && pMouseX <= x + (i.pos.x * 10) + 9 && pMouseY <= y + (i.pos.y * 10) + 9) {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 11, 0, 10, 10);
                } else {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 0, 0, 10, 10);
                }
            } else if (i.type == 1) {
                if (pMouseX >= x + (i.pos.x * 10) && pMouseY >= y + (i.pos.y * 10) && pMouseX <= x + (i.pos.x * 10) + 9 && pMouseY <= y + (i.pos.y * 10) + 9) {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 11, 22+(i.essence*11), 10, 10);
                } else {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 0, 22+(i.essence*11), 10, 10);
                }
            } else if (i.type == 2) {
                if (pMouseX >= x + (i.pos.x * 10) && pMouseY >= y + (i.pos.y * 10) && pMouseX <= x + (i.pos.x * 10) + 9 && pMouseY <= y + (i.pos.y * 10) + 9) {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 33, 22+(i.essence*11), 10, 10);
                } else {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 22, 22+(i.essence*11), 10, 10);
                }
            } else if (i.type == 3) {
                if (pMouseX >= x + (i.pos.x * 10) && pMouseY >= y + (i.pos.y * 10) && pMouseX <= x + (i.pos.x * 10) + 9 && pMouseY <= y + (i.pos.y * 10) + 9) {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 55, 22+(i.essence*11), 10, 10);
                } else {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 44, 22+(i.essence*11), 10, 10);
                }
            }
        }
    }
    public Vector2d startLine;
    public Vector2d endLine;
    @Override
    public void mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        for (Tile i : tiles.values()) {
            if (i.type == 1) {
                if (pMouseX >= (i.pos.x * 10) && pMouseY >= (i.pos.y * 10) && pMouseX <= (i.pos.x * 10) + 9 && pMouseY <= (i.pos.y * 10) + 9) {
                    if (pButton == 0) {
                        startLine = new Vector2d((i.pos.x*10)+5, (i.pos.y*10)+5);
                        endLine = new Vector2d((i.pos.x*10)+5+pDragX, (i.pos.y*10)+5+pDragY);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(double pMouseX, double pMouseY, int pButton) {
        startLine = null;
        endLine = null;
    }

    @Override
    public void mouseClicked(double pMouseX, double pMouseY, int pButton) {
        super.mouseClicked(pMouseX, pMouseY, pButton);
        for (Tile i : tiles.values()) {
            if (pMouseX >= (i.pos.x * 10) && pMouseY >= (i.pos.y * 10) && pMouseX <= (i.pos.x * 10) + 9 && pMouseY <= (i.pos.y * 10) + 9) {
                if (i.type == 0 || i.type == 3) {
                    i.connectedTo = null;
                }
                break;
            }
        }
    }
    public class Tile {
        public Vector2i pos;
        public int type;
        public int essence;
        public Vector2i connectedTo;
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
