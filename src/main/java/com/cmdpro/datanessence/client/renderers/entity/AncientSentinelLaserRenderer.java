package com.cmdpro.datanessence.client.renderers.entity;

import com.cmdpro.databank.misc.RenderingUtil;
import com.cmdpro.datanessence.entity.AncientSentinel;
import com.cmdpro.datanessence.entity.AncientSentinelLaser;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class AncientSentinelLaserRenderer extends EntityRenderer<AncientSentinelLaser> {

    public AncientSentinelLaserRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }
    public ResourceLocation getTextureLocation(AncientSentinelLaser pEntity) {
        return null;
    }

    @Override
    public boolean shouldRender(AncientSentinelLaser livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
    @Override
    public void render(AncientSentinelLaser entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        if (entity.getEnd() != null) {
            float progress = Math.clamp((float)entity.time/(float)entity.maxTime, 0f, 1f);
            float scale = 1f-progress;

            poseStack.pushPose();
            double d0 = Mth.lerp(partialTick, entity.xOld, entity.getX());
            double d1 = Mth.lerp(partialTick, entity.yOld, entity.getY());
            double d2 = Mth.lerp(partialTick, entity.zOld, entity.getZ());
            Vec3 posOffset = new Vec3(d0, d1, d2).subtract(entity.position());
            Vec3 pos = entity.position().add(posOffset);
            poseStack.translate(-pos.x, -pos.y, -pos.z);
            RenderingUtil.renderAdvancedBeaconBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, partialTick, 1.0f, entity.level().getGameTime(), entity.position(), entity.getEnd(), AncientSentinelLaser.getColor(entity.level()), 0.25f*scale, 0.3f*scale);
            poseStack.popPose();
        }
    }
}