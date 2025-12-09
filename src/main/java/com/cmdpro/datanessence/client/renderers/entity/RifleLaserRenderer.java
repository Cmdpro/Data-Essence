package com.cmdpro.datanessence.client.renderers.entity;

import com.cmdpro.databank.misc.RenderingUtil;
import com.cmdpro.datanessence.client.shaders.DataNEssenceRenderTypes;
import com.cmdpro.datanessence.entity.RifleLaser;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Math;
import net.minecraft.client.renderer.RenderType;

import java.awt.*;
import java.util.Random;

public class RifleLaserRenderer extends EntityRenderer<RifleLaser> {
    //texture: 128x64, 16x8 glyphs of 8x8
    private static final ResourceLocation ANCIENT_FONT_TEXTURE =
            ResourceLocation.parse("datanessence:textures/font/ancient.png");

    // Text-style render type (position + color + uv + light)
    private static final RenderType ANCIENT_FONT_TYPE =
            RenderType.text(ANCIENT_FONT_TEXTURE);

    // 16x8 grid of 8x8 glyphs baked from ancient.png
    private static final int FONT_COLS = 16;
    private static final int FONT_ROWS = 8;

    //headache 😔
    private static final long[] ANCIENT_GLYPH_MASKS = {
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0001000101010101L, 0x0000000000000505L, 0x000A0A1F0A1F0A0AL,
            0x00040F100E011E04L, 0x0011120204080911L, 0x0016090D16040A04L, 0x0000000000000101L,
            0x0004020101010204L, 0x0001020404040201L, 0x0000000000050205L, 0x000004041F040400L,
            0x0101000000000000L, 0x000000001F000000L, 0x000E11040E1B0A00L, 0x0001020204080810L,
            0x000E0004000E0000L, 0x0000000400000000L, 0x0000001100000000L, 0x0011000004000000L,
            0x0011000000110000L, 0x000A001100040000L, 0x000A0011000A0000L, 0x0001010101010000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0004081F08040000L, 0x0101000000010000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000E1101010EL, 0x000E110E00040000L, 0x000E010E110E0000L, 0x0000040808000000L,
            0x000000001F000000L, 0x0000000000010204L, 0x00001F001F000000L, 0x00000000070C0000L,
            0x0005151F15110000L, 0x000E111111110000L, 0x000E010101010000L, 0x00020A0A0A0E0000L,
            0x00020505050F0000L, 0x000E010E010E0000L, 0x000E010E01010000L, 0x000E010D11110000L,
            0x0011111F11110000L, 0x000E040404040000L, 0x00080808080E0000L, 0x00090B0D09090000L,
            0x00010101010F0000L, 0x0015151515110000L, 0x0011111111110000L, 0x000E1111110E0000L,
            0x000F11110F010000L, 0x000E11110E080800L, 0x000F11110F090000L, 0x000E100E011E0000L,
            0x0004040404170000L, 0x00111111110E0000L, 0x0009090A0C0C0000L, 0x00151115151A0000L,
            0x00110A040A110000L, 0x0011110A04040000L, 0x001F0204081F0000L, 0x000E040404040E00L,
            0x0010080402010000L, 0x000E020202020E00L, 0x00040A1100000000L, 0x000000000000001FL,
            0x0002010000000000L, 0x000D130F010F0000L, 0x000712120F010100L, 0x00070F080F000000L,
            0x000F010F10100F00L, 0x000F01130F000000L, 0x0004070604040000L, 0x00060A0A0F010F0EL,
            0x0013131F01010100L, 0x000F040404000400L, 0x0008080808000806L, 0x00150D0705050100L,
            0x0004040404040200L, 0x001B151515010000L, 0x001313130F010000L, 0x000E11110E000000L,
            0x000F11110F01010FL, 0x000E12120E1010E0L, 0x00070F0808010000L, 0x000E100E011E0000L,
            0x001F040404040400L, 0x0011111915060000L, 0x00090A0C0C000000L, 0x0015111A1A000000L,
            0x00090A040A090000L, 0x000A0A0E010E0000L, 0x001F0204081F0000L, 0x00000E0E18180000L,
            0x00040C0404040000L, 0x00000E0707000000L, 0x0004081020000000L, 0x0000000010100000L,
            0x001008081C080800L, 0x0000000000000000L, 0x0010081E09090600L, 0x0004011510040000L,
            0x00040E150E040000L, 0x00141A111A141010L, 0x00110E0012150900L, 0x0006040D05110E00L,
            0x0006040405110E00L, 0x000E11110A001F00L, 0x0016191102070000L, 0x00151B111B150000L,
            0x0016181011030000L, 0x001A0F0A1E0B0000L, 0x0004080A1F0A0204L, 0x001F11110A000400L,
            0x00040A080C180E01L, 0x001F11150E040400L, 0x0002080A150A0208L, 0x001E110101130000L,
            0x00111814120F0000L, 0x0007010101010107L, 0x0010080804020201L, 0x0007040404040407L,
            0x0000000000110A04L, 0x1F00000000000000L, 0x0000000000000201L, 0x000A11110E001F00L,
            0x000E111116101010L, 0x00050B110B050000L, 0x000E111310181018L, 0x000A11111F000400L,
            0x00040A03120F0000L, 0x000C14140E050506L, 0x0010081E09090600L, 0x0004011510040000L,
            0x00040E150E040000L, 0x00141A111A141010L, 0x00110E0012150900L, 0x0006040D05110E00L,
            0x0006040405110E00L, 0x000E11110A001F00L, 0x0016191102070000L, 0x00151B111B150000L,
            0x0016181011030000L, 0x001A0F0A1E0B0000L, 0x0004080A1F0A0204L, 0x001F11110A000400L,
            0x00040A080C180E01L, 0x001F11150E040400L, 0x0002080A150A0208L, 0x001E110101130000L,
            0x00111814120F0000L, 0x0004020201020204L, 0x0001010101010101L, 0x0001020204020201L,
            0x0000000000001926L, 0x0000000000000000L
    };



    public RifleLaserRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(RifleLaser pEntity) {
        return null;
    }

    @Override
    public boolean shouldRender(RifleLaser livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(RifleLaser entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        renderBeamFor(entity, poseStack, bufferSource, partialTick);
    }

    // BASE BEAM RENDER
    public static void renderBeam(PoseStack poseStack, MultiBufferSource bufferSource,
                                  Vec3 start, Vec3 end, float thickness) {

        VertexConsumer vertexConsumer = bufferSource.getBuffer(DataNEssenceRenderTypes.SOLID_COLOR);
        Color color = new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color);

        poseStack.pushPose();
        poseStack.translate(start.x, start.y, start.z);
        poseStack.pushPose();
        RenderingUtil.rotateStackToPoint(poseStack, start, end);

        Vec3 start2 = Vec3.ZERO;
        Vec3 end2 = new Vec3(0, end.distanceTo(start), 0);

        Vector3f diff = end2.toVector3f()
                .sub(start2.toVector3f())
                .normalize()
                .mul(thickness, thickness, thickness);

        Vector3f diffRotated = new Vector3f(diff).rotateX(Math.toRadians(90));

//        for (int i = 0; i < 4; i++) {
//            Vector3f offset = new Vector3f(diffRotated).rotateY(Math.toRadians(90 * i));
//            Vector3f offset2 = new Vector3f(diffRotated).rotateY(Math.toRadians(90 * (i + 1)));
//
//            vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset2)).setColor(color.getRGB());
//            vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset)).setColor(color.getRGB());
//            vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset)).setColor(color.getRGB());
//            vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset2)).setColor(color.getRGB());
//        }

//        Vector3f offset = new Vector3f(diffRotated);
//        Vector3f offset2 = new Vector3f(diffRotated).rotateY(Math.toRadians(90));
//        Vector3f offset3 = new Vector3f(diffRotated).rotateY(Math.toRadians(180));
//        Vector3f offset4 = new Vector3f(diffRotated).rotateY(Math.toRadians(270));
//
//        vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset4)).setColor(color.getRGB());
//        vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset3)).setColor(color.getRGB());
//        vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset2)).setColor(color.getRGB());
//        vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset)).setColor(color.getRGB());
//
//        vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset)).setColor(color.getRGB());
//        vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset2)).setColor(color.getRGB());
//        vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset3)).setColor(color.getRGB());
//        vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset4)).setColor(color.getRGB());

        poseStack.popPose();
        poseStack.popPose();
    }

    // MAIN COMBINED RENDER
    public static void renderBeamFor(RifleLaser entity, PoseStack poseStack,
                                     MultiBufferSource bufferSource, float partialTick) {

        if (entity.getEnd() != null) {

            float progress = Math.clamp(
                    (float) ((entity.time - 10) + partialTick) / (float) (entity.maxTime - 10),
                    0f, 1f
            );
            float scale = 1f - progress;

            poseStack.pushPose();

            double d0 = Mth.lerp(partialTick, entity.xOld, entity.getX());
            double d1 = Mth.lerp(partialTick, entity.yOld, entity.getY());
            double d2 = Mth.lerp(partialTick, entity.zOld, entity.getZ());
            Vec3 posOffset = new Vec3(d0, d1, d2).subtract(entity.position());
            Vec3 pos = entity.position().add(posOffset);
            poseStack.translate(-pos.x, -pos.y, -pos.z);

            // base beam
            renderBeam(poseStack, bufferSource, entity.position(), entity.getEnd(), 0.3f * scale);

            // electricity
            renderElectricity(poseStack, bufferSource,
                    entity.position(), entity.getEnd(), scale, entity.time + partialTick);

            // sparks
            renderBeamSparks(poseStack, bufferSource,
                    entity.position(), entity.getEnd(),
                    scale, entity.time + partialTick);

            // rings
            renderRings(poseStack, bufferSource,
                    entity.position(), entity.getEnd(), scale, entity.time + partialTick);

            poseStack.popPose();
        }
    }

    //SPARK RENDERING
    private static void renderBeamSparks(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            Vec3 start, Vec3 end,
            float scale,
            float time
    ) {
        VertexConsumer consumer = bufferSource.getBuffer(DataNEssenceRenderTypes.SOLID_COLOR);

        Color baseColor = new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color);
        int r = Math.min(255, baseColor.getRed() + 80);
        int g = Math.min(255, baseColor.getGreen() + 80);
        int bl = Math.min(255, baseColor.getBlue() + 80);
        int color = (230 << 24) | (r << 16) | (g << 8) | bl;

        float length = (float) end.distanceTo(start);

        poseStack.pushPose();
        poseStack.translate(start.x, start.y, start.z);
        RenderingUtil.rotateStackToPoint(poseStack, start, end);

        Random rand = new Random((long) (time * 100));

        int sparkCount = 20;
        float maxOut = 0.9f * scale;
        float sparkLen = 0.4f * scale;

        for (int i = 0; i < sparkCount; i++) {

            // Random along full length of beam
            float t = rand.nextFloat();
            float y = t * length;

            // Random angle around beam
            float angle = rand.nextFloat() * ((float)Math.PI * 2f);

            // Spark base offset around beam
            float offX = Mth.cos(angle) * maxOut;
            float offZ = Mth.sin(angle) * maxOut;

            // Spark direction (normalized outward vector)
            Vector3f dir = new Vector3f(offX, 0, offZ).normalize();

            // END of spark = starting point + direction * sparkLen
            Vector3f p0 = new Vector3f(offX, y, offZ);                 // spark base
            Vector3f p1 = new Vector3f(dir).mul(sparkLen).add(p0);     // spark tip

            float w = 0.03f * scale;

            // perpendicular for width
            Vector3f perp = new Vector3f(-dir.z, 0, dir.x).normalize().mul(w);

            Vector3f a = new Vector3f(p0).add(perp);
            Vector3f b = new Vector3f(p0).sub(perp);
            Vector3f c = new Vector3f(p1).sub(perp);
            Vector3f d = new Vector3f(p1).add(perp);

            // front quad
            consumer.addVertex(poseStack.last(), a).setColor(color);
            consumer.addVertex(poseStack.last(), b).setColor(color);
            consumer.addVertex(poseStack.last(), c).setColor(color);
            consumer.addVertex(poseStack.last(), d).setColor(color);

            // back quad
            consumer.addVertex(poseStack.last(), d).setColor(color);
            consumer.addVertex(poseStack.last(), c).setColor(color);
            consumer.addVertex(poseStack.last(), b).setColor(color);
            consumer.addVertex(poseStack.last(), a).setColor(color);
        }


        poseStack.popPose();
    }

    // ELECTRICITY
    private static void renderElectricity(PoseStack poseStack, MultiBufferSource bufferSource,
                                          Vec3 start, Vec3 end, float scale, float time) {

        VertexConsumer vertexConsumer = bufferSource.getBuffer(DataNEssenceRenderTypes.SOLID_COLOR);

        Color baseColor = new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color);
        int Brightness = 0;
        int r = Math.min(255, baseColor.getRed() + Brightness);
        int g = Math.min(255, baseColor.getGreen() + Brightness);
        int b = Math.min(255, baseColor.getBlue() + Brightness);
        int colorRGB = (255 << 24) | (r << 16) | (g << 8) | b; //dont ever ask me about this

        float length = (float) end.distanceTo(start);

        poseStack.pushPose();
        poseStack.translate(start.x, start.y, start.z);
        RenderingUtil.rotateStackToPoint(poseStack, start, end);

        Random random = new Random();

        int arcs = 5;
        float segmentSize = 0.8f;
        int segments = (int) (length / segmentSize);
        if (segments < 1) segments = 1;

        float maxOffset = 0.7f * scale;

        for (int a = 0; a < arcs; a++) {

            Vector3f prevPos = new Vector3f(0, 0, 0);

            for (int i = 1; i <= segments; i++) {
                float progress = (float) i / segments;
                float y = progress * length;

                float x = (random.nextFloat() - 0.5f) * 2 * maxOffset;
                float z = (random.nextFloat() - 0.5f) * 2 * maxOffset;

                if (i == segments) { x = 0; z = 0; }

                Vector3f nextPos = new Vector3f(x, y, z);

                drawElectricitySegment(
                        poseStack, vertexConsumer,
                        prevPos, nextPos,
                        0.06f * scale, colorRGB
                );

                prevPos = nextPos;
            }
        }

        poseStack.popPose();
    }

    private static void drawElectricitySegment(PoseStack poseStack, VertexConsumer consumer,
                                               Vector3f start, Vector3f end, float width, int color) {

        consumer.addVertex(poseStack.last(), start.x - width, start.y, start.z).setColor(color);
        consumer.addVertex(poseStack.last(), start.x + width, start.y, start.z).setColor(color);
        consumer.addVertex(poseStack.last(), end.x + width, end.y, end.z).setColor(color);
        consumer.addVertex(poseStack.last(), end.x - width, end.y, end.z).setColor(color);

        consumer.addVertex(poseStack.last(), start.x, start.y, start.z - width).setColor(color);
        consumer.addVertex(poseStack.last(), start.x, start.y, start.z + width).setColor(color);
        consumer.addVertex(poseStack.last(), end.x, end.y, end.z + width).setColor(color);
        consumer.addVertex(poseStack.last(), end.x, end.y, end.z - width).setColor(color);

        consumer.addVertex(poseStack.last(), end.x - width, end.y, end.z).setColor(color);
        consumer.addVertex(poseStack.last(), end.x + width, end.y, end.z).setColor(color);
        consumer.addVertex(poseStack.last(), start.x + width, start.y, start.z).setColor(color);
        consumer.addVertex(poseStack.last(), start.x - width, start.y, start.z).setColor(color);

        consumer.addVertex(poseStack.last(), end.x, end.y, end.z - width).setColor(color);
        consumer.addVertex(poseStack.last(), end.x, end.y, end.z + width).setColor(color);
        consumer.addVertex(poseStack.last(), start.x, start.y, start.z + width).setColor(color);
        consumer.addVertex(poseStack.last(), start.x, start.y, start.z - width).setColor(color);
    }

    // RINGS
    private static void renderRings(PoseStack poseStack, MultiBufferSource bufferSource,
                                    Vec3 start, Vec3 end, float timeScale, float time) {

        VertexConsumer vc = bufferSource.getBuffer(DataNEssenceRenderTypes.SOLID_COLOR);
        Color baseColor = new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color);

        float length = (float) end.distanceTo(start);

        poseStack.pushPose();
        poseStack.translate(start.x, start.y, start.z);
        RenderingUtil.rotateStackToPoint(poseStack, start, end);

        int rings = 6;

        float baseRadius = 3.0f;
        float baseThickness = 0.7f; // ring thickness

        long baseSeed = Double.doubleToLongBits(start.x)
                ^ (Double.doubleToLongBits(start.y) * 31L)
                ^ (Double.doubleToLongBits(start.z) * 131L);

        float rotationSpeed = -0.1f;
        float rotationBase = rotationSpeed * time;

        for (int ringIndex = 0; ringIndex < rings; ringIndex++) {

            float t = (rings == 1) ? 0.0f : (float) ringIndex / (rings - 1);

            float radius = baseRadius * (1.0f - t) * timeScale;
            float thickness = baseThickness * (1.0f - t) * timeScale;
            float y = t * length;

            if (radius <= 0.001f || thickness <= 0.001f) continue;

            float alphaF = 0.4f + 0.6f * (1.0f - t);
            int a = (int) (alphaF * 255.0f);
            int r = baseColor.getRed();
            int g = baseColor.getGreen();
            int b = baseColor.getBlue();
            int argb = (a << 24) | (r << 16) | (g << 8) | b;

            // use only non-empty rows
            int usableRows = FONT_ROWS - 2;
            int row = 2 + (usableRows > 0 ? (ringIndex % usableRows) : 0);

            float circumference = (float) (2.0 * Math.PI * radius);
            int chars = Mth.clamp((int) (circumference / (thickness * 1.3f)), 10, 32);
            float angleStep = (float) (2.0 * Math.PI / chars);

            float ringRotation = rotationBase + ringIndex * 0.4f;

            Random rand = new Random(baseSeed + ringIndex * 31L);

            float innerR = radius - thickness * 0.5f;
            float outerR = radius + thickness * 0.5f;

            for (int c = 0; c < chars; c++) {

                // pick a random non-empty glyph in this row if possible
                long mask = 0L;
                int glyphIndex = 0;
                int attempts = 0;

                while (attempts < 8 && mask == 0L) {
                    int col = rand.nextInt(FONT_COLS);
                    glyphIndex = row * FONT_COLS + col;
                    mask = ANCIENT_GLYPH_MASKS[glyphIndex];
                    attempts++;
                }
                if (mask == 0L) {
                    continue; // no pixels in this glyph, skip
                }

                float a0 = c * angleStep + ringRotation;
                float a1 = (c + 1) * angleStep + ringRotation;
                float da = a1 - a0;

                // subdivide this segment into 8x8 cells and draw those with bits set
                for (int gy = 0; gy < 8; gy++) {
                    for (int gx = 0; gx < 8; gx++) {
                        int bit = gy * 8 + gx;
                        if (((mask >> bit) & 1L) == 0L) continue;

                        float tAngle0 = gx / 8.0f;
                        float tAngle1 = (gx + 1) / 8.0f;
                        float tRad0 = gy / 8.0f;
                        float tRad1 = (gy + 1) / 8.0f;

                        float angleL = a0 + da * tAngle0;
                        float angleR = a0 + da * tAngle1;

                        float r0 = innerR + thickness * tRad0;
                        float r1 = innerR + thickness * tRad1;

                        float x0 = Mth.cos(angleL) * r0;
                        float z0 = Mth.sin(angleL) * r0;
                        float x1 = Mth.cos(angleR) * r0;
                        float z1 = Mth.sin(angleR) * r0;
                        float x2 = Mth.cos(angleR) * r1;
                        float z2 = Mth.sin(angleR) * r1;
                        float x3 = Mth.cos(angleL) * r1;
                        float z3 = Mth.sin(angleL) * r1;

                        // front face
                        vc.addVertex(poseStack.last(), x0, y, z0).setColor(argb);
                        vc.addVertex(poseStack.last(), x1, y, z1).setColor(argb);
                        vc.addVertex(poseStack.last(), x2, y, z2).setColor(argb);
                        vc.addVertex(poseStack.last(), x3, y, z3).setColor(argb);

                        // back face
                        vc.addVertex(poseStack.last(), x3, y, z3).setColor(argb);
                        vc.addVertex(poseStack.last(), x2, y, z2).setColor(argb);
                        vc.addVertex(poseStack.last(), x1, y, z1).setColor(argb);
                        vc.addVertex(poseStack.last(), x0, y, z0).setColor(argb);
                    }
                }
            }
        }

        poseStack.popPose();
    }


}
