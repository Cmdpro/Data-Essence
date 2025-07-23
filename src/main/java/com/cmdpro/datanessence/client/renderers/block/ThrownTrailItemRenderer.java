package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.misc.TrailRender;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.datanessence.entity.ThrownTrailItemProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ThrownTrailItemRenderer extends ThrownItemRenderer<ThrownTrailItemProjectile> {
    public ThrownTrailItemRenderer(EntityRendererProvider.Context context, float scale, boolean fullBright) {
        super(context, scale, fullBright);
    }

    public ThrownTrailItemRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ThrownTrailItemProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        TrailRender trail = entity.getTrail();
        if (trail != null) {
            poseStack.pushPose();
            double d0 = Mth.lerp(partialTicks, entity.xOld, entity.getX());
            double d1 = Mth.lerp(partialTicks, entity.yOld, entity.getY());
            double d2 = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
            Vec3 posOffset = new Vec3(d0, d1, d2).subtract(entity.position());
            Vec3 pos = entity.position().add(posOffset);
            poseStack.translate(-pos.x, -pos.y, -pos.z);
            trail.position = entity.getBoundingBox().getCenter().add(posOffset);
            trail.render(poseStack, RenderHandler.createBufferSource(), LightTexture.FULL_BRIGHT, entity.getGradient());
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
