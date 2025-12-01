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

import java.awt.*;
import java.util.Random;

public class RifleLaserRenderer extends EntityRenderer<RifleLaser> {

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

    // ------------------------------------------------------------------------------------------
    // BASE BEAM RENDER
    // ------------------------------------------------------------------------------------------
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

    // ------------------------------------------------------------------------------------------
    // MAIN COMBINED RENDER
    // ------------------------------------------------------------------------------------------
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

            // *** NEW SPARKS ***
            renderBeamSparks(poseStack, bufferSource,
                    entity.position(), entity.getEnd(),
                    scale, entity.time + partialTick);

            // rings
            renderRings(poseStack, bufferSource,
                    entity.position(), entity.getEnd(), scale, entity.time + partialTick);

            poseStack.popPose();
        }
    }

    // ------------------------------------------------------------------------------------------
    // *** NEW SPARK RENDERING METHOD ***
    // ------------------------------------------------------------------------------------------
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

    // ------------------------------------------------------------------------------------------
    // ELECTRICITY (unchanged)
    // ------------------------------------------------------------------------------------------
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

    // ------------------------------------------------------------------------------------------
    // RINGS (unchanged)
    // ------------------------------------------------------------------------------------------
    private static void renderRings(PoseStack poseStack, MultiBufferSource bufferSource,
                                    Vec3 start, Vec3 end, float timeScale, float time) {

        VertexConsumer vertexConsumer = bufferSource.getBuffer(DataNEssenceRenderTypes.SOLID_COLOR);
        Color baseColor = new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color);

        float length = (float) end.distanceTo(start);

        poseStack.pushPose();
        poseStack.translate(start.x, start.y, start.z);
        RenderingUtil.rotateStackToPoint(poseStack, start, end);

        int rings = 6;
        int segments = 24;

        float baseRadius = 3f;
        float baseThickness = 0.3f;

        float rotationBase = time * 3f;
        float stepDeg = 360f / segments;

        for (int i = 0; i < rings; i++) {

            float t = (float) i / (rings - 1);
            float r = baseRadius * (1 - t) * timeScale;
            float rt = baseThickness * (1 - t) * timeScale;
            float y = t * length;

            float alpha = 0.4f + 0.6f * (1 - t);
            int color = ((int) (alpha * 255) << 24) | (baseColor.getRGB() & 0x00FFFFFF);

            float angleOffsetDeg = rotationBase + i * 30f;

            for (int s = 0; s < segments; s++) {

                float a0 = (float) Math.toRadians(s * stepDeg + angleOffsetDeg);
                float a1 = (float) Math.toRadians((s + 1) * stepDeg + angleOffsetDeg);

                float x0 = (float) Math.cos(a0) * r;
                float z0 = (float) Math.sin(a0) * r;
                float x1 = (float) Math.cos(a1) * r;
                float z1 = (float) Math.sin(a1) * r;

                float x0out = x0 + rt * (float) Math.cos(a0);
                float z0out = z0 + rt * (float) Math.sin(a0);
                float x1out = x1 + rt * (float) Math.cos(a1);
                float z1out = z1 + rt * (float) Math.sin(a1);

                Vector3f p0 = new Vector3f(x0, y, z0);
                Vector3f p1 = new Vector3f(x1, y, z1);
                Vector3f p1out = new Vector3f(x1out, y, z1out);
                Vector3f p0out = new Vector3f(x0out, y, z0out);

                vertexConsumer.addVertex(poseStack.last(), p0).setColor(color);
                vertexConsumer.addVertex(poseStack.last(), p1).setColor(color);
                vertexConsumer.addVertex(poseStack.last(), p1out).setColor(color);
                vertexConsumer.addVertex(poseStack.last(), p0out).setColor(color);

                vertexConsumer.addVertex(poseStack.last(), p0out).setColor(color);
                vertexConsumer.addVertex(poseStack.last(), p1out).setColor(color);
                vertexConsumer.addVertex(poseStack.last(), p1).setColor(color);
                vertexConsumer.addVertex(poseStack.last(), p0).setColor(color);
            }
        }

        poseStack.popPose();
    }
}
