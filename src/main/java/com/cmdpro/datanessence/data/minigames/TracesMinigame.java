package com.cmdpro.datanessence.data.minigames;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.databank.Minigame;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TracesMinigame extends Minigame {
    public static final ResourceLocation TEXTURE = DataNEssence.locate("textures/gui/data_bank_minigames.png");
    public TracesMinigame(Map<Vector2i, Tile> tiles, int size) {
        this.size = size;
        setupTiles();
        this.tiles.putAll(tiles);
    }
    public void setupTiles() {
        this.tiles = new HashMap<>();
        for (int i = -size; i <= size; i++) {
            for (int o = -size; o <= size; o++) {
                Tile tile = new Tile();
                Vector2i pos = new Vector2i(((150/10)/2)+i, ((150/10)/2)+o);
                tile.pos = pos;
                tiles.put(pos, tile);
            }
        }
    }
    @Override
    public boolean isFinished() {
        boolean finished = true;
        List<Vector2i> unfilledTiles = new ArrayList<>(tiles.keySet().stream().toList());
        for (Map.Entry<Vector2i, Tile> i : tiles.entrySet()) {
            if (i.getValue().type == 1 || i.getValue().type == 2) {
                unfilledTiles.remove(i.getKey());
            }
            if (i.getValue().type == 1) {
                for (Vector2i j : i.getValue().path) {
                    unfilledTiles.remove(j);
                }
                if (!isConnectedToEnd(i.getValue())) {
                    finished = false;
                    break;
                }
            }
        }
        if (!unfilledTiles.isEmpty()) {
            return false;
        }
        return finished;
    }
    public boolean isConnectedToEnd(Tile tile) {
        if (tile.type == 1) {
            if (tile.path.isEmpty()) {
                return false;
            }
            Tile tile2 = getTile(tile.path.getLast());
            if (tile2 != null) {
                return tile2.type == 2;
            }
        } else {
            return true;
        }
        return false;
    }
    public HashMap<Vector2i, Tile> tiles;
    public int size;
    public Tile getTile(Vector2i pos) {
        return tiles.get(pos);
    }
    public Tile getTile(Vector2d pos) {
        return tiles.get(new Vector2i((int)Math.floor(pos.x/10d), (int)Math.floor(pos.y/10d)));
    }

    @Override
    public void tick() {
        super.tick();
        time++;
    }

    private int time;
    @Override
    public void render(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y) {
        boolean allConnectedToEnds = true;
        List<Vector2i> pathedTiles = new ArrayList<>();
        for (Tile i : tiles.values()) {
            if (i.type == 1 && !isConnectedToEnd(i)) {
                allConnectedToEnds = false;
            }
            if (i.type == 1) {
                pathedTiles.addAll(i.path);
            }
        }
        for (Tile i : tiles.values()) {
            if (i.type == 0) {
                if (pMouseX >= x + (i.pos.x * 10) && pMouseY >= y + (i.pos.y * 10) && pMouseX <= x + (i.pos.x * 10) + 9 && pMouseY <= y + (i.pos.y * 10) + 9) {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 33, 11, 10, 10);
                } else {
                    pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10), y + (i.pos.y * 10), 22, 11, 10, 10);
                }
                if (allConnectedToEnds) {
                    if (!pathedTiles.contains(i.pos)) {
                        float[] originalColor = RenderSystem.getShaderColor().clone();
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f-((((float)Math.sin(Math.toRadians((time+pPartialTick)*4f))/2f)+1f)/2f));
                        RenderSystem.enableBlend();
                        pGuiGraphics.blit(TEXTURE, x + (i.pos.x * 10) + 4, y + (i.pos.y * 10) + 2, 44, 0, 2, 6);
                        RenderSystem.setShaderColor(originalColor[0], originalColor[1], originalColor[2], originalColor[3]);
                        RenderSystem.disableBlend();
                    }
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
            if (!i.path.isEmpty()) {
                Vector2i last = null;
                for (Vector2i j : i.path) {
                    if (last == null) {
                        last = j;
                        continue;
                    }
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
                    drawLine(new Vector2d(x + ((double) last.x * 10d) + 6d, y + ((double) last.y * 10d) + 6d), new Vector2d(x + ((double) j.x * 10d) + 6d, y + ((double) j.y * 10d) + 6d), color);
                    last = j;
                }
            }
        }
        if (!path.isEmpty()) {
            int color = pathColor;
            List<Vector2i> pathSoFar = new ArrayList<>();
            for (Vector2i j : path) {
                for (Map.Entry<Vector2i, Tile> k : tiles.entrySet()) {
                    if (!k.getKey().equals(start)) {
                        if (k.getValue().path.contains(j) || (k.getKey().equals(j) && (k.getValue().type == 1))) {
                            color = 0xFF0000;
                        }
                    }
                }
                if (pathSoFar.contains(j)) {
                    color = 0xFF0000;
                }
                pathSoFar.add(j);
            }
            Tile endTile = getTile(pathSoFar.getLast());
            if (endTile.type == 2) {
                if (endTile.essence != pathEssence) {
                    color = 0xFF0000;
                }
            }
            Vector2i last = null;
            for (Vector2i j : path) {
                if (last == null) {
                    last = j;
                    continue;
                }
                drawLine(new Vector2d(x + ((double) last.x * 10d) + 6d, y + ((double) last.y * 10d) + 6d), new Vector2d(x + ((double) j.x * 10d) + 6d, y + ((double) j.y * 10d) + 6d), color);
                last = j;
            }
        }
    }
    public Vector2i start;
    public List<Vector2i> path = new ArrayList<>();
    public Vector2i current;
    public int pathColor;
    public int pathEssence;
    @Override
    public void mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        if (pButton == 0) {
            for (Tile i : tiles.values()) {
                if (pMouseX >= (i.pos.x * 10) && pMouseY >= (i.pos.y * 10) && pMouseX <= (i.pos.x * 10) + 9 && pMouseY <= (i.pos.y * 10) + 9) {
                    if (current != i.pos) {
                        if (!path.contains(i.pos)) {
                            if (current != null) {
                                if (i.pos.equals(current.x + 1, current.y) ||
                                        i.pos.equals(current.x - 1, current.y) ||
                                        i.pos.equals(current.x, current.y + 1) ||
                                        i.pos.equals(current.x, current.y - 1)) {
                                    if (getTile(path.getLast()).type != 2) {
                                        if (!path.contains(i.pos)) {
                                            path.addLast(i.pos);
                                            current = i.pos;
                                        }
                                    }
                                }
                            }
                        } else if (path.size() >= 2) {
                            if (path.get(path.size()-2).equals(i.pos)) {
                                path.removeLast();
                                current = i.pos;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            boolean valid = true;
            for (Tile i : tiles.values()) {
                if (pMouseX >= (i.pos.x * 10) && pMouseY >= (i.pos.y * 10) && pMouseX <= (i.pos.x * 10) + 9 && pMouseY <= (i.pos.y * 10) + 9) {
                    if (i.type != 2) {
                        valid = false;
                    }
                }
            }
            if (valid) {
                if (!path.isEmpty()) {
                    List<Vector2i> pathSoFar = new ArrayList<>();
                    for (Vector2i j : path) {
                        for (Map.Entry<Vector2i, Tile> k : tiles.entrySet()) {
                            if (!k.getKey().equals(start)) {
                                if (k.getValue().path.contains(j) || (k.getKey().equals(j) && (k.getValue().type == 1))) {
                                    valid = false;
                                }
                            }
                        }
                        if (pathSoFar.contains(j)) {
                            valid = false;
                        }
                        pathSoFar.add(j);
                    }
                    Tile endTile = getTile(pathSoFar.getLast());
                    if (endTile.type == 2) {
                        if (endTile.essence != pathEssence) {
                            valid = false;
                        }
                    }
                    if (valid) {
                        tiles.get(start).path = new ArrayList<>(path);
                    }
                }
            }
            path.clear();
            start = null;
            current = null;
        }
    }
    public void drawLine(Vector2d start, Vector2d end, int color) {
        GlStateManager._depthMask(false);
        GlStateManager._disableCull();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        Tesselator tess = RenderSystem.renderThreadTesselator();
        RenderSystem.lineWidth(2f*(float)Minecraft.getInstance().getWindow().getGuiScale());
        BufferBuilder buf = tess.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        Vec2 vec = new Vec2((float)start.x-(float)end.x, (float)start.y-(float)end.y).normalized();
        buf.addVertex((float)start.x, (float)start.y, 0.0F).setColor(color).setNormal(vec.x, vec.y, 0);
        buf.addVertex((float)end.x, (float)end.y, 0.0F).setColor(color).setNormal(vec.x, vec.y, 0);
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
                    if (i.type == 1) {
                        if (i.essence == 0) {
                            pathColor = 0xFFe236ef;
                        }
                        if (i.essence == 1) {
                            pathColor = 0xFFf5fbc0;
                        }
                        if (i.essence == 2) {
                            pathColor = 0xFF57f36c;
                        }
                        if (i.essence == 3) {
                            pathColor = 0xFFe7fcf9;
                        }
                        pathEssence = i.essence;
                        start = i.pos;
                        i.path.clear();
                        path.clear();
                        path.add(start);
                        current = start;
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
        public List<Vector2i> path = new ArrayList<>();
    }
    public class Client {

    }

    @Override
    public String getLocalizationKey() {
        return "data_tablet.databank_minigame_traces";
    }
}
