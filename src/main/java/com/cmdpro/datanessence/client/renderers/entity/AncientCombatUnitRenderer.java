package com.cmdpro.datanessence.client.renderers.entity;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.entity.DatabankLivingEntityModel;
import com.cmdpro.databank.model.entity.DatabankLivingEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.AncientSentinel;
import com.cmdpro.datanessence.entity.ancientcombatunit.AncientCombatUnit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;


public class AncientCombatUnitRenderer extends DatabankLivingEntityRenderer<AncientCombatUnit> {
    public AncientCombatUnitRenderer(EntityRendererProvider.Context p_272933_) {
        super(p_272933_, new Model(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(AncientCombatUnit pEntity) {
        return DataNEssence.locate("textures/entity/ancient_combat_unit.png");
    }

    public static class Model extends DatabankLivingEntityModel<AncientCombatUnit> {
        public DatabankModel model;
        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("ancient_combat_unit"));
            }
            return model;
        }
        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/entity/ancient_combat_unit.png");
        }

        @Override
        public void setupModelPose(AncientCombatUnit pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            animate(pEntity.animState);
            if (pEntity.animState.isCurrentAnim("idle") || pEntity.animState.isCurrentAnim("walk")) {
                Vec2 rot = new Vec2(Mth.lerp(partialTick, pEntity.xRotO, pEntity.getXRot()), Mth.rotLerp(partialTick, pEntity.yHeadRotO, pEntity.yHeadRot));
                float bodyRot = Mth.rotLerp(partialTick, pEntity.yBodyRotO, pEntity.yBodyRot);
                modelPose.stringToPart.get("head").rotation.x = rot.x * (float) (Math.PI / 180.0);
                modelPose.stringToPart.get("head").rotation.y = (rot.y-bodyRot) * (float) (Math.PI / 180.0);
            }
        }

        @Override
        public void renderModel(AncientCombatUnit pEntity, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, int pColor, Vec3 normalMult) {
            super.renderModel(pEntity, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor, normalMult);
        }
    }
}
