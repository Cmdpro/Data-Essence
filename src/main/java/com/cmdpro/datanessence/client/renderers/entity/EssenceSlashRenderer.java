package com.cmdpro.datanessence.client.renderers.entity;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.EssenceSlashProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class EssenceSlashRenderer extends EntityRenderer<EssenceSlashProjectile> {

    public EssenceSlashRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(EssenceSlashProjectile pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.pushPose();
        pPoseStack.translate(0, 0.5f, 0);
        Vec2 rot = calculateRotationVector(new Vec3(0, 0, 0), pEntity.getDeltaMovement());
        pPoseStack.mulPose(Axis.YP.rotation((-rot.y-90) * Mth.DEG_TO_RAD));
        pPoseStack.mulPose(Axis.ZP.rotation(-rot.x * Mth.DEG_TO_RAD));
        pPoseStack.mulPose(Axis.XP.rotation(pEntity.rotation * Mth.DEG_TO_RAD));
        PoseStack.Pose posestack$pose = pPoseStack.last();
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(pEntity)));
        vertex(vertexconsumer, posestack$pose, pPackedLight, 0.0F, 0, 0, 1);
        vertex(vertexconsumer, posestack$pose, pPackedLight, 1.0F, 0, 1, 1);
        vertex(vertexconsumer, posestack$pose, pPackedLight, 1.0F, 1, 1, 0);
        vertex(vertexconsumer, posestack$pose, pPackedLight, 0.0F, 1, 0, 0);
        pPoseStack.popPose();
    }

    private static void vertex(VertexConsumer pConsumer, PoseStack.Pose pPose, int pPackedLight, float pX, int pY, int pU, int pV) {
        pConsumer.addVertex(pPose, pX - 0.5F, (float)pY - 0.5f, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv((float)pU, (float)pV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(pPackedLight)
                .setNormal(pPose, 0.0F, 1.0F, 0.0F);
    }
    public Vec2 calculateRotationVector(Vec3 pVec, Vec3 pTarget) {
        double d0 = pTarget.x - pVec.x;
        double d1 = pTarget.y - pVec.y;
        double d2 = pTarget.z - pVec.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return new Vec2(
                Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)))),
                Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F)
        );
    }
    public ResourceLocation getTextureLocation(EssenceSlashProjectile pEntity) {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/entity/essence_slash.png");
    }
}