package com.cmdpro.datanessence.client.renderers.entity;

import com.cmdpro.databank.misc.RenderingUtil;
import com.cmdpro.datanessence.client.shaders.DataNEssenceRenderTypes;
import com.cmdpro.datanessence.entity.AncientSentinelLaser;
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

public class RifleLaserRenderer extends EntityRenderer<RifleLaser> {

    public RifleLaserRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }
    public ResourceLocation getTextureLocation(RifleLaser pEntity) {
        return null;
    }

    @Override
    public boolean shouldRender(RifleLaser livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
    @Override
    public void render(RifleLaser entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        renderBeamFor(entity, poseStack, bufferSource, partialTick);
    }
    public static void renderBeam(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 start, Vec3 end, float thickness) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(DataNEssenceRenderTypes.SOLID_COLOR);
        Color color = new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color);

        poseStack.pushPose();
        poseStack.translate(start.x, start.y, start.z);
        poseStack.pushPose();
        RenderingUtil.rotateStackToPoint(poseStack, start, end);
        Vec3 start2 = Vec3.ZERO;
        Vec3 end2 = new Vec3(0, end.distanceTo(start), 0);

        Vector3f diff = end2.toVector3f().sub(start2.toVector3f()).normalize().mul(thickness, thickness, thickness);
        Vector3f diffRotated = new Vector3f(diff).rotateX(Math.toRadians(90));
        for (int i = 0; i < 4; i++) {
            Vector3f offset = new Vector3f(diffRotated).rotateY(Math.toRadians(90*i));
            Vector3f offset2 = new Vector3f(diffRotated).rotateY(Math.toRadians(90*(i+1)));
            vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset2)).setColor(color.getRGB());
            vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset)).setColor(color.getRGB());
            vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset)).setColor(color.getRGB());
            vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset2)).setColor(color.getRGB());
        }
        Vector3f offset = new Vector3f(diffRotated);
        Vector3f offset2 = new Vector3f(diffRotated).rotateY(Math.toRadians(90));
        Vector3f offset3 = new Vector3f(diffRotated).rotateY(Math.toRadians(180));
        Vector3f offset4 = new Vector3f(diffRotated).rotateY(Math.toRadians(270));
        vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset4)).setColor(color.getRGB());
        vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset3)).setColor(color.getRGB());
        vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset2)).setColor(color.getRGB());
        vertexConsumer.addVertex(poseStack.last(), start2.toVector3f().add(offset)).setColor(color.getRGB());

        vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset)).setColor(color.getRGB());
        vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset2)).setColor(color.getRGB());
        vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset3)).setColor(color.getRGB());
        vertexConsumer.addVertex(poseStack.last(), end2.toVector3f().add(offset4)).setColor(color.getRGB());

        poseStack.popPose();
        poseStack.popPose();
    }
    public static void renderBeamFor(RifleLaser entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
        if (entity.getEnd() != null) {
            float progress = Math.clamp((float)((entity.time-10)+partialTick)/(float)(entity.maxTime-10), 0f, 1f);
            float scale = 1f-progress;

            poseStack.pushPose();
            double d0 = Mth.lerp(partialTick, entity.xOld, entity.getX());
            double d1 = Mth.lerp(partialTick, entity.yOld, entity.getY());
            double d2 = Mth.lerp(partialTick, entity.zOld, entity.getZ());
            Vec3 posOffset = new Vec3(d0, d1, d2).subtract(entity.position());
            Vec3 pos = entity.position().add(posOffset);
            poseStack.translate(-pos.x, -pos.y, -pos.z);

            renderBeam(poseStack, bufferSource, entity.position(), entity.getEnd(), 0.3f*scale);

            poseStack.popPose();
        }
    }
}