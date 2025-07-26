package com.cmdpro.datanessence.data.minigames;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.databank.Minigame;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LaserMinigame extends Minigame {
    public static final ResourceLocation TEXTURE = DataNEssence.locate("textures/gui/data_bank_minigames.png");
    public LaserMinigame(Map<Vector2i, Tile> tiles) {
        setupTiles();
        this.tiles.putAll(tiles);
        rebuildBeams();
    }
    public void setupTiles() {
        this.tiles = new HashMap<>();
    }
    @Override
    public boolean isFinished() {
        return allEndsConnected;
    }
    public HashMap<Vector2i, Tile> tiles;
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
    public List<Beam> beams = new ArrayList<>();
    public void rebuildBeams() {
        beams.clear();
        for (Tile i : tiles.values()) {
            Beam build = i.onBeamBuild(this);
            if (build != null) {
                beams.add(build);
            }
        }
        List<Beam> handledBeams = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            HashMap<Vector2i, List<Beam>> beamEnds = new HashMap<>();
            for (Beam i : beams) {
                if (handledBeams.contains(i)) {
                    continue;
                }
                if (i.end != null) {
                    List<Beam> beamList = beamEnds.getOrDefault(i.end, new ArrayList<>());
                    beamList.add(i);
                    beamEnds.put(i.end, beamList);
                }
                handledBeams.add(i);
            }
            for (Map.Entry<Vector2i, List<Beam>> i : beamEnds.entrySet()) {
                Tile tile = getTile(i.getKey());
                tile.handleBeamEnds(this, i.getValue());
            }
            if (handledBeams.size() >= beams.size()) {
                break;
            }
        }
        boolean allGood = true;
        for (Tile i : tiles.values()) {
            if (i.getType() == Tile.TileType.GOAL) {
                boolean hasBeam = false;
                for (Beam j : beams) {
                    if (j.end != null) {
                        if (j.end.equals(i.pos)) {
                            hasBeam = true;
                        }
                    }
                }
                if (!hasBeam) {
                    allGood = false;
                    break;
                }
            }
        }
        allEndsConnected = allGood;
    }

    private int time;
    public boolean allEndsConnected;
    @Override
    public void render(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y) {
        for (int i = 0; i < 15; i++) {
            for (int o = 0; o < 15; o++) {
                if (pMouseX >= x + (i * 10) && pMouseY >= y + (o * 10) && pMouseX <= x + (i * 10) + 9 && pMouseY <= y + (o * 10) + 9) {
                    pGuiGraphics.blit(TEXTURE, x + (i * 10), y + (o * 10), 0, 77, 10, 10);
                } else {
                    pGuiGraphics.blit(TEXTURE, x + (i * 10), y + (o * 10), 0, 66, 10, 10);
                }
            }
        }
        for (Beam beam : beams) {
            if (!beam.beamParts.isEmpty()) {
                Beam.BeamSegment first = beam.beamParts.getFirst();
                Beam.BeamSegment last = beam.beamParts.getLast();
                List<Beam.BeamSegment> segmentPoints = new ArrayList<>();
                for (Beam.BeamSegment i : beam.beamParts) {
                    boolean add = false;
                    if (i == first || i == last) {
                        add = true;
                    } else {
                        if (i.beamRenderOffsetChange.isPresent()) {
                            add = true;
                        }
                        if (i.colorChange.isPresent()) {
                            add = true;
                        }
                        if (i.wasDirectionChanged) {
                            add = true;
                        }
                    }
                    if (add) {
                        segmentPoints.add(i);
                    }
                }
                if (segmentPoints.getFirst() != null && segmentPoints.getFirst().colorChange.isPresent()) {
                    Beam.BeamSegment lastSegment = null;
                    BeamColor color = segmentPoints.getFirst().colorChange.get();
                    for (Beam.BeamSegment i : segmentPoints) {
                        Vec2 point = new Vec2(x + (i.pos.x * 10), y + (i.pos.y * 10)).add(5).add(i.beamRenderOffsetChange.orElse(Vec2.ZERO));
                        BeamColor lastColor = color;
                        if (i.colorChange.isPresent()) {
                            color = i.colorChange.get();
                        }
                        if (lastSegment != null) {
                            Vec2 from = new Vec2(x + (lastSegment.pos.x * 10), y + (lastSegment.pos.y * 10)).add(5).add(lastSegment.beamRenderOffsetChange.orElse(Vec2.ZERO));
                            drawLine(from.add(new Vec2(1.1f, 0.6f)), point.add(new Vec2(1.1f, 0.6f)), lastColor.color.getRGB());
                        }
                        lastSegment = i;
                    }
                }
            }
        }
        for (Tile i : tiles.values()) {
            PoseStack stack = pGuiGraphics.pose();
            stack.pushPose();
            stack.translate(x + (i.pos.x * 10)+5, y + (i.pos.y * 10)+5, 0);
            stack.mulPose(Axis.ZP.rotationDegrees(i.getRotationAngle()));
            boolean hovered = pMouseX >= x + (i.pos.x * 10) && pMouseY >= y + (i.pos.y * 10) && pMouseX <= x + (i.pos.x * 10) + 9 && pMouseY <= y + (i.pos.y * 10) + 9;
            i.render(screen, pGuiGraphics, pPartialTick, pMouseX, pMouseY, -5, -5, hovered);
            stack.popPose();
        }
        for (Tile i : tiles.values()) {
            PoseStack stack = pGuiGraphics.pose();
            stack.pushPose();
            stack.translate(x + (i.pos.x * 10)+5, y + (i.pos.y * 10)+5, 0);
            stack.mulPose(Axis.ZP.rotationDegrees(i.getRotationAngle()));
            boolean hovered = pMouseX >= x + (i.pos.x * 10) && pMouseY >= y + (i.pos.y * 10) && pMouseX <= x + (i.pos.x * 10) + 9 && pMouseY <= y + (i.pos.y * 10) + 9;
            i.renderPost(screen, pGuiGraphics, pPartialTick, pMouseX, pMouseY, x + (i.pos.x * 10), y + (i.pos.y * 10), hovered);
            stack.popPose();
        }
    }
    public void drawLine(Vec2 start, Vec2 end, int color) {
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
                    if (i.canPlayerRotate()) {
                        i.rotate(1);
                        rebuildBeams();
                    }
                    break;
                }
            }
        }
    }
    public void addBeam(Beam beam) {
        if (beam != null) {
            beams.add(beam);
        }
    }
    public static class Tile {
        public Vector2i pos;
        public int type;
        public int color;
        public int rotation;
        public Tile(Vector2i pos, int type, int color) {
            this(pos, type, color, getFirstValidRotation(type).getIndex());
        }
        public Tile(Vector2i pos, int type, int color, int rotation) {
            this.pos = pos;
            this.type = type;
            this.color = color;
            this.rotation = rotation;
        }
        public Tile(Vector2i pos, TileType type, BeamColor color) {
            this(pos, type, color, getFirstValidRotation(type.getIndex()));
        }
        public Tile(Vector2i pos, TileType type, BeamColor color, TileRotation rotation) {
            this(pos, type.getIndex(), color.getIndex(), rotation.getIndex());
        }
        public BeamColor getColor() {
            return BeamColor.getColor(color);
        }
        public TileRotation getRotation() {
            return TileRotation.getRotation(rotation);
        }
        public TileType getType() {
            return TileType.getType(type);
        }
        public TileRotation[] getValidRotations() {
            return getValidRotations(type);
        }
        public static TileRotation[] getValidRotations(int type) {
            return TileType.getType(type).validRotations;
        }
        public static TileRotation getFirstValidRotation(int type) {
            Optional<TileRotation> rotation = Arrays.stream(getValidRotations(type)).findFirst();
            return rotation.orElse(TileRotation.UP);
        }
        public boolean canPlayerRotate() {
            return canPlayerRotate(getType());
        }
        public float getRotationAngle() {
            if (getType() == TileType.MIRROR) {
                return rotation == 3 ? -90 : 0;
            }
            return getRotation().rotation;
        }
        public static boolean canPlayerRotate(TileType type) {
            return type.canPlayerRotate;
        }
        public void rotate(int amount) {
            TileRotation[] rotations = getValidRotations();
            int index = Arrays.stream(rotations).toList().indexOf(getRotation());
            int rotation = (index+amount)%rotations.length;
            if (rotation < 0) {
                rotation = rotations.length+rotation;
            }
            this.rotation = rotations[rotation].getIndex();
        }
        public void render(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y, boolean hovered) {
            int u = switch (getType()) {
                case EMITTER -> 11;
                case GOAL -> 22;
                case MIRROR -> 33;
                case SPLITTER -> 44;
                case PRISM -> 55;
                case FILTER -> 66;
                case WALL -> 77;
            };
            int v = 66;
            boolean colorOverlay = switch (getType()) {
                case EMITTER, GOAL, FILTER -> true;
                default -> false;
            };
            if (hovered) {
                pGuiGraphics.blit(TEXTURE, x, y, u, v+11, 10, 10);
            } else {
                pGuiGraphics.blit(TEXTURE, x, y, u, v, 10, 10);
            }
            if (colorOverlay) {
                float[] color = RenderSystem.getShaderColor().clone();
                Color tileColor = getColor().color;
                float red = (float)tileColor.getRed()/255f;
                float green = (float)tileColor.getGreen()/255f;
                float blue = (float)tileColor.getBlue()/255f;
                float alpha = (float)tileColor.getAlpha()/255f;
                RenderSystem.setShaderColor(red, green, blue, alpha);
                pGuiGraphics.blit(TEXTURE, x, y, u, v+22, 10, 10);
                RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
            }
        }
        public void renderPost(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y, boolean hovered) {

        }
        public Beam.BeamModificationResult modifyBeam(LaserMinigame minigame, Beam beam, int depth) {
            Optional<BeamColor> beamColor = Optional.empty();
            Optional<TileRotation> beamRotation = Optional.empty();
            Optional<Vec2> beamRenderOffset = Optional.empty();
            boolean shouldStop = false;
            boolean reachedEnd = false;
            if (getType() == TileType.GOAL) {
                shouldStop = true;
                if (beam.color == color && beam.rotation == getRotation().getOpposite()) {
                    reachedEnd = true;
                }
            }
            if (getType() == TileType.MIRROR) {
                beamRotation = Optional.of(switch (beam.rotation) {
                    case UP -> TileRotation.RIGHT;
                    case LEFT -> TileRotation.DOWN;
                    case DOWN -> TileRotation.LEFT;
                    case RIGHT -> TileRotation.UP;
                });
                if (getRotation() == TileRotation.LEFT) {
                    beamRotation = Optional.of(beamRotation.get().getOpposite());
                }
            }
            if (getType() == TileType.SPLITTER) {
                shouldStop = true;
                if (beam.rotation == getRotation().getOpposite()) {
                    Beam beam1 = Beam.buildBeam(minigame, new Vector2i(pos), beam.getColor(), getRotation().getRight(), Vec2.ZERO, depth+1);
                    Beam beam2 = Beam.buildBeam(minigame, new Vector2i(pos), beam.getColor(), getRotation().getLeft(), Vec2.ZERO, depth+1);
                    minigame.addBeam(beam1);
                    minigame.addBeam(beam2);
                }
            }
            if (getType() == TileType.PRISM) {
                if (beam.rotation == getRotation().getOpposite()) {
                    BeamColor originalBeamColor = beam.getColor();
                    if (originalBeamColor.color.getRed() > 0) {
                        Beam redBeam = Beam.buildBeam(minigame, new Vector2i(pos), BeamColor.RED, getRotation().getOpposite().getLeft(), Vec2.ZERO, depth+1);
                        minigame.addBeam(redBeam);
                    }
                    if (originalBeamColor.color.getGreen() > 0) {
                        Beam greenBeam = Beam.buildBeam(minigame, new Vector2i(pos), BeamColor.GREEN, getRotation().getOpposite(), Vec2.ZERO, depth+1);
                        minigame.addBeam(greenBeam);
                    }
                    if (originalBeamColor.color.getBlue() > 0) {
                        Beam blueBeam = Beam.buildBeam(minigame, new Vector2i(pos), BeamColor.BLUE, getRotation().getOpposite().getRight(), Vec2.ZERO, depth+1);
                        minigame.addBeam(blueBeam);
                    }
                } else {
                    reachedEnd = true;
                }
                shouldStop = true;
            }
            if (getType() == TileType.FILTER) {
                boolean red = false;
                boolean green = false;
                boolean blue = false;
                if (getColor().color.getRed() > 0) {
                    red = true;
                }
                if (getColor().color.getGreen() > 0) {
                    green = true;
                }
                if (getColor().color.getBlue() > 0) {
                    blue = true;
                }
                beamColor = Optional.of(BeamColor.get(red, green, blue));
            }
            if (getType() == TileType.WALL) {
                shouldStop = true;
            }
            return new Beam.BeamModificationResult(beamColor, beamRotation, beamRenderOffset, shouldStop, reachedEnd);
        }
        public void handleBeamEnds(LaserMinigame minigame, List<Beam> beams) {
            if (getType() == TileType.PRISM) {
                boolean red = false;
                boolean green = false;
                boolean blue = false;
                for (Beam i : beams) {
                    if (i.rotation != getRotation().getOpposite()) {
                        if (i.getColor().color.getRed() > 0) {
                            red = true;
                        }
                        if (i.getColor().color.getGreen() > 0) {
                            green = true;
                        }
                        if (i.getColor().color.getBlue() > 0) {
                            blue = true;
                        }
                    }
                }
                BeamColor output = BeamColor.get(red, green, blue);
                if (output != null) {
                    minigame.addBeam(Beam.buildBeam(minigame, pos, output, getRotation(), Vec2.ZERO));
                }
            }
        }
        public Beam onBeamBuild(LaserMinigame minigame) {
            if (getType() == TileType.EMITTER) {
                return Beam.buildBeam(minigame, pos, getColor(), getRotation(), Vec2.ZERO);
            }
            return null;
        }
        public enum TileRotation implements StringRepresentable {
            UP("up", new Vector2i(0, -1), 0, 2, 3, 1),
            RIGHT("right", new Vector2i(1, 0), 90, 3, 0, 2),
            DOWN("down", new Vector2i(0, 1), 180, 0, 1, 3),
            LEFT("left", new Vector2i(-1, 0), -90, 1, 2, 0);
            public final String name;
            public final Vector2i direction;
            public final float rotation;
            public final int opposite, left, right;
            TileRotation(String name, Vector2i direction, float rotation, int opposite, int left, int right) {
                this.name = name;
                this.direction = direction;
                this.rotation = rotation;
                this.opposite = opposite;
                this.left = left;
                this.right = right;
            }
            public TileRotation getOpposite() {
                return getRotation(opposite);
            }
            public TileRotation getLeft() {
                return getRotation(left);
            }
            public TileRotation getRight() {
                return getRotation(right);
            }
            public int getIndex() {
                return ordinal();
            }
            public static TileRotation getRotation(int rotation) {
                TileRotation[] values = TileRotation.values();
                if (values.length > rotation) {
                    return values[rotation];
                }
                return null;
            }
            public static TileRotation[] noRotation() {
                return new TileRotation[] { TileRotation.UP };
            }
            public static TileRotation[] allRotation() {
                return TileRotation.values();
            }
            public static TileRotation[] leftRight() {
                return new TileRotation[] { TileRotation.LEFT, TileRotation.RIGHT };
            }
            public static TileRotation[] upDown() {
                return new TileRotation[] { TileRotation.UP, TileRotation.DOWN };
            }
            public static TileRotation get(String name) {
                for (TileRotation i : TileRotation.values()) {
                    if (i.name.equals(name)) {
                        return i;
                    }
                }
                return null;
            }
            @Override
            public String getSerializedName() {
                return name;
            }
        }
        public enum TileType implements StringRepresentable {
            EMITTER("emitter", false, TileRotation.allRotation()),
            GOAL("goal", false, TileRotation.allRotation()),
            MIRROR("mirror", true, TileRotation.leftRight()),
            SPLITTER("splitter", true, TileRotation.allRotation()),
            PRISM("prism", true, TileRotation.allRotation()),
            FILTER("filter", false, TileRotation.noRotation()),
            WALL("wall", false, TileRotation.noRotation());
            public final boolean canPlayerRotate;
            public final TileRotation[] validRotations;
            public final String name;
            TileType(String name, boolean canPlayerRotate, TileRotation[] validRotations) {
                this.canPlayerRotate = canPlayerRotate;
                this.validRotations = validRotations;
                this.name = name;
            }
            public int getIndex() {
                return ordinal();
            }
            public static TileType getType(int type) {
                TileType[] values = TileType.values();
                if (values.length > type) {
                    return values[type];
                }
                return null;
            }
            public static TileType get(String name) {
                for (TileType i : TileType.values()) {
                    if (i.name.equals(name)) {
                        return i;
                    }
                }
                return null;
            }

            @Override
            public String getSerializedName() {
                return name;
            }
        }
    }
    public static class Beam {
        public int color;
        public Tile.TileRotation rotation;
        public boolean stopped;
        public List<BeamSegment> beamParts = new ArrayList<>();
        public Vector2i end;
        public Beam(int color, Tile.TileRotation rotation) {
            this.color = color;
            this.rotation = rotation;
        }
        public BeamColor getColor() {
            return BeamColor.getColor(color);
        }
        public static Beam buildBeam(LaserMinigame minigame, Vector2i start, int color, Tile.TileRotation rotation, Vec2 firstOffset) {
            return buildBeam(minigame, start, BeamColor.getColor(color), rotation, firstOffset);
        }
        public static Beam buildBeam(LaserMinigame minigame, Vector2i start, BeamColor color, Tile.TileRotation rotation, Vec2 firstOffset) {
            return buildBeam(minigame, start, color, rotation, firstOffset, 0);
        }
        public static Beam buildBeam(LaserMinigame minigame, Vector2i start, BeamColor color, Tile.TileRotation rotation, Vec2 firstOffset, int depth) {
            if (depth > 5) {
                return null;
            }
            Beam beam = new Beam(color.getIndex(), rotation);
            Vector2i pos = new Vector2i(start);
            beam.beamParts.clear();
            for (int i = 0; i < 100; i++) {
                boolean colorChanged = false;
                boolean offsetChanged = false;
                boolean wasDirectionChanged = false;
                Tile tile = i > 0 ? minigame.getTile(pos) : null;
                Vec2 offset = i > 0 ? Vec2.ZERO : firstOffset;
                if (i == 0) {
                    offsetChanged = true;
                    colorChanged = true;
                }
                if (tile != null) {
                    BeamModificationResult result = tile.modifyBeam(minigame, beam, depth);
                    if (result.rotation.isPresent()) {
                        beam.rotation = result.rotation.get();
                        wasDirectionChanged = true;
                    }
                    if (result.color.isPresent()) {
                        beam.color = result.color.get().getIndex();
                        colorChanged = true;
                    }
                    if (result.beamRenderOffset.isPresent()) {
                        offset = result.beamRenderOffset.get();
                        offsetChanged = true;
                    }
                    if (result.shouldStop) {
                        beam.stopped = true;
                    }
                    if (result.reachedEnd) {
                        beam.end = pos;
                    }
                } else if (pos.x < 0 || pos.y < 0 || pos.x >= 15 || pos.y >= 15) {
                    beam.stopped = true;
                }
                beam.beamParts.add(new BeamSegment(new Vector2i(pos), colorChanged ? Optional.ofNullable(BeamColor.getColor(beam.color)) : Optional.empty(), offsetChanged ? Optional.of(offset) : Optional.empty(), wasDirectionChanged));
                if (beam.stopped) {
                    break;
                }
                pos.add(beam.rotation.direction);
            }
            return beam;
        }
        public record BeamSegment(Vector2i pos, Optional<BeamColor> colorChange, Optional<Vec2> beamRenderOffsetChange, boolean wasDirectionChanged) {}
        public record BeamModificationResult(Optional<BeamColor> color, Optional<Tile.TileRotation> rotation, Optional<Vec2> beamRenderOffset, boolean shouldStop, boolean reachedEnd) {}
    }
    public enum BeamColor implements StringRepresentable {
        RED("red", Color.RED),
        GREEN("green", Color.GREEN),
        BLUE("blue", Color.BLUE),
        CYAN("cyan", Color.CYAN),
        MAGENTA("magenta", Color.MAGENTA),
        YELLOW("yellow", Color.YELLOW),
        WHITE("white", Color.WHITE);

        public final String name;
        public final Color color;
        BeamColor(String name, Color color) {
            this.name = name;
            this.color = color;
        }
        public int getIndex() {
            return ordinal();
        }
        public static BeamColor getColor(int color) {
            BeamColor[] values = BeamColor.values();
            if (values.length > color) {
                return values[color];
            }
            return null;
        }
        public static BeamColor get(boolean red, boolean green, boolean blue) {
            for (BeamColor i : BeamColor.values()) {
                Color color = i.color;
                if ((red && color.getRed() <= 0) || (!red && color.getRed() > 0)) {
                    continue;
                }
                if ((green && color.getGreen() <= 0) || (!green && color.getGreen() > 0)) {
                    continue;
                }
                if ((blue && color.getBlue() <= 0) || (!blue && color.getBlue() > 0)) {
                    continue;
                }
                return i;
            }
            return null;
        }
        public static BeamColor get(String name) {
            for (BeamColor i : BeamColor.values()) {
                if (i.name.equals(name)) {
                    return i;
                }
            }
            return null;
        }
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
