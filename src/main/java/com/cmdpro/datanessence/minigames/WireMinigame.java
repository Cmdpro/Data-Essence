package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.screen.databank.Minigame;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WireMinigame extends Minigame {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_bank_minigames.png");
    public WireMinigame(Map<Vector2i, Tile> tiles) {
        setupTiles();
        this.tiles.putAll(tiles);
    }
    public void setupTiles() {
        this.tiles = new HashMap<>();
        for (int i = 0; i < 150/10; i++) {
            for (int o = 0; o < 150 / 10; o++) {
                Tile tile = new Tile();
                Vector2i pos = new Vector2i(i, o);
                tile.pos = pos;
                tiles.put(pos, tile);
            }
        }
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
    public Tile getTile(Vector2d pos) {
        return tiles.get(new Vector2i((int)Math.floor(pos.x/10d), (int)Math.floor(pos.y/10d)));
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
        for (Tile i : tiles.values()) {
            if (i.connectedTo != null) {
                int color = 0x00000000;
                if (i.essence == 0) {
                    color = 0xFFe236ef;
                }
                if (i.essence == 1) {
                    color = 0xFFf5fbc0;
                }
                if (i.essence == 2) {
                    color = 0xFF57f36c;
                }
                if (i.essence == 3) {
                    color = 0xFFe7fcf9;
                }
                drawLine(new Vector2d(x+((double)i.pos.x*10d)+6d, y+((double)i.pos.y*10d)+6d), new Vector2d(x+((double)i.connectedTo.x*10d)+6d, y+((double)i.connectedTo.y*10d)+6d), color);
            }
        }
        if (startLine != null && endLine != null) {
            drawLine(new Vector2d(startLine.x+1d+(double)x, startLine.y+1d+(double)y), new Vector2d(endLine.x+1d+(double)x, endLine.y+1d+(double)y), lineColor);
        }
    }
    public Vector2d startLine;
    public Vector2d endLine;
    public int lineColor;
    @Override
    public void mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        if (pButton == 0) {
            if (endLine != null) {
                endLine.add(pDragX, pDragY);
            }
        }
    }

    @Override
    public void mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            endLine = new Vector2d(pMouseX, pMouseY);
            if (startLine != null) {
                Tile start = getTile(startLine);
                Tile end = getTile(endLine);
                if (start != end) {
                    if (start != null && end != null) {
                        if (start.essence == end.essence) {
                            boolean invalid = false;
                            Line2D line = new Line2D.Double((start.pos.x * 10) + 5, (start.pos.y * 10) + 5, (end.pos.x * 10) + 5, (end.pos.y * 10) + 5);
                            for (Tile i : tiles.values()) {
                                if (i.connectedTo != null) {
                                    if (!i.connectedTo.equals(start.pos) && !i.pos.equals(end.pos)) {
                                        Line2D line2 = new Line2D.Double((i.pos.x * 10) + 5, (i.pos.y * 10) + 5, (i.connectedTo.x * 10) + 5, (i.connectedTo.y * 10) + 5);
                                        if (line.intersectsLine(line2)) {
                                            invalid = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!invalid) {
                                if (start.connectedTo == null) {
                                    if (end.type == 3 || end.type == 2) {
                                        start.connectedTo = end.pos;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            startLine = null;
            endLine = null;
        }
    }
    public void drawLine(Vector2d start, Vector2d end, int color) {
        GlStateManager._depthMask(false);
        GlStateManager._disableCull();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        Tesselator tess = RenderSystem.renderThreadTesselator();
        RenderSystem.lineWidth(1f*(float)Minecraft.getInstance().getWindow().getGuiScale());
        BufferBuilder buf = tess.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        float x1 = (float)start.x;
        float y1 = (float)start.y;
        float x2 = (float)end.x;
        float y2 = (float)end.y;
        Vec2 vec1 = new Vec2(x1 >= x2 ? x1 == x2 ? 0 : 1 : -1, y1 >= y2 ? y1 == y2 ? 0 : 1 : -1);
        Vec2 vec2 = new Vec2(x2 >= x1 ? x1 == x2 ? 0 : 1 : -1, y2 >= y1 ? y1 == y2 ? 0 : 1 : -1);
        buf.addVertex(x1, y1, 0.0f).setColor(color).setNormal(vec1.x, vec1.y, 0);
        buf.addVertex(x2, y2, 0.0f).setColor(color).setNormal(vec2.x, vec2.y, 0);
        BufferUploader.drawWithShader(buf.buildOrThrow());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GlStateManager._enableCull();
        GlStateManager._depthMask(true);
        RenderSystem.lineWidth(1);
    }

    @Override
    public void mouseClicked(double pMouseX, double pMouseY, int pButton) {
        super.mouseClicked(pMouseX, pMouseY, pButton);
        if (pButton == 0) {
            for (Tile i : tiles.values()) {
                if (pMouseX >= (i.pos.x * 10) && pMouseY >= (i.pos.y * 10) && pMouseX <= (i.pos.x * 10) + 9 && pMouseY <= (i.pos.y * 10) + 9) {
                    if (i.type == 1 || i.type == 3) {
                        i.connectedTo = null;
                        if (i.essence == 0) {
                            lineColor = 0xFFe236ef;
                        }
                        if (i.essence == 1) {
                            lineColor = 0xFFf5fbc0;
                        }
                        if (i.essence == 2) {
                            lineColor = 0xFF57f36c;
                        }
                        if (i.essence == 3) {
                            lineColor = 0xFFe7fcf9;
                        }
                        startLine = new Vector2d((i.pos.x * 10) + 5, (i.pos.y * 10) + 5);
                        endLine = new Vector2d(pMouseX, pMouseY);
                    }
                    break;
                }
            }
        }
    }
    public static class Tile {
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
