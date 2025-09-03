package com.cmdpro.datanessence.client.renderers.entity;

import com.cmdpro.databank.misc.RenderingUtil;
import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.entity.DatabankEntityModel;
import com.cmdpro.databank.model.entity.DatabankLivingEntityModel;
import com.cmdpro.databank.model.entity.DatabankLivingEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.AncientSentinel;
import com.cmdpro.datanessence.entity.AncientSentinelLaser;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;


public class AncientSentinelRenderer extends DatabankLivingEntityRenderer<AncientSentinel> {
    public AncientSentinelRenderer(EntityRendererProvider.Context p_272933_) {
        super(p_272933_, new Model(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(AncientSentinel pEntity) {
        return DataNEssence.locate("textures/entity/ancient_sentinel.png");
    }

    @Override
    public boolean shouldRender(AncientSentinel livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(AncientSentinel pEntity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(pEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        int shootTime = pEntity.laserTime;
        if (shootTime >= 0) {
            Vec3 end = getEnd(pEntity);
            float scale = Math.clamp((float)shootTime/(float)pEntity.maxLaserTime, 0f, 1f);

            poseStack.pushPose();
            double d0 = Mth.lerp(partialTick, pEntity.xOld, pEntity.getX());
            double d1 = Mth.lerp(partialTick, pEntity.yOld, pEntity.getY());
            double d2 = Mth.lerp(partialTick, pEntity.zOld, pEntity.getZ());
            Vec3 posOffset = new Vec3(d0, d1, d2).subtract(pEntity.position());
            Vec3 pos = pEntity.position().add(posOffset);
            poseStack.translate(-pos.x, -pos.y, -pos.z);
            RenderingUtil.renderAdvancedBeaconBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, partialTick, 1.0f, pEntity.level().getGameTime(), pEntity.getEyePosition(), end, AncientSentinelLaser.getColor(pEntity.level()), 0, 0.5f*scale);
            poseStack.popPose();
        }
    }
    private HitResult getHit(AncientSentinel sentinel, double hitDistance, boolean hitFluids) {
        Vec3 vec3 = sentinel.getEyePosition();
        Vec3 vec31 = sentinel.getEyePosition().vectorTo(sentinel.laserTarget).normalize();
        Vec3 vec32 = vec3.add(vec31.x * hitDistance, vec31.y * hitDistance, vec31.z * hitDistance);
        return sentinel.level().clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, hitFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, sentinel));
    }
    private Vec3 getEnd(AncientSentinel sentinel) {
        HitResult result = getHit(sentinel, 20, false);
        return result.getLocation();
    }

    public static class Model extends DatabankLivingEntityModel<AncientSentinel> {
        public DatabankModel model;
        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("ancient_sentinel"));
            }
            return model;
        }
        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/entity/ancient_sentinel.png");
        }

        @Override
        public void setupModelPose(AncientSentinel pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            animate(pEntity.animState);
            Vec2 rot = new Vec2(Mth.lerp(partialTick, pEntity.xRotO, pEntity.getXRot()), Mth.rotLerp(partialTick, pEntity.yHeadRotO, pEntity.yHeadRot));
            float bodyRot = Mth.rotLerp(partialTick, pEntity.yBodyRotO, pEntity.yBodyRot);
            modelPose.stringToPart.get("head").rotation.x = rot.x * (float) (Math.PI / 180.0);
            modelPose.stringToPart.get("head").rotation.y = (rot.y-bodyRot) * (float) (Math.PI / 180.0);
        }
    }
}
