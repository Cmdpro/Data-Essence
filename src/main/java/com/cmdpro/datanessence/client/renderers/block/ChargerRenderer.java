package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.auxiliary.Charger;
import com.cmdpro.datanessence.block.auxiliary.ChargerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;


public class ChargerRenderer extends DatabankBlockEntityRenderer<ChargerBlockEntity> {
    public ChargerRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    @Override
    public void render(ChargerBlockEntity charger, float pPartialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Direction facing = charger.getBlockState().getValue(Charger.FACING);
        Vec3 rotateAround = new Vec3(0.5, 0.5, 0.5);

        if (facing.equals(Direction.NORTH))
            poseStack.rotateAround(Axis.YP.rotationDegrees(0), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.SOUTH))
            poseStack.rotateAround(Axis.YP.rotationDegrees(180), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.EAST))
            poseStack.rotateAround(Axis.YP.rotationDegrees(-90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.WEST))
            poseStack.rotateAround(Axis.YP.rotationDegrees(90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);


        super.render(charger, pPartialTick, poseStack, pBufferSource, pPackedLight, pPackedOverlay);
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(charger.getLevel().getLevelData().getGameTime() % 360));
        poseStack.scale(0.25F, 0.25F, 0.25F);
        Minecraft.getInstance().getItemRenderer().renderStatic(charger.item, ItemDisplayContext.FIXED, pPackedLight, pPackedOverlay, poseStack, pBufferSource, charger.getLevel(), 0);
        poseStack.popPose();
    }


    public static class Model extends DatabankBlockEntityModel<ChargerBlockEntity> {
        public DatabankModel model;

        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/charger.png");
        }

        @Override
        public void setupModelPose(ChargerBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            DatabankAnimationState state = pEntity.animState;
            if (!pEntity.item.isEmpty()) {
                if (state.isCurrentAnim("idle_empty")) {
                    state.setAnim("extend_exciters");
                }
                if (pEntity.charging) {
                    if (state.isCurrentAnim("idle_exciters_out")) {
                        state.setAnim("orb_rise");
                    }
                } else {
                    if (state.isCurrentAnim("orb_spin") || state.isCurrentAnim("orb_rise")) {
                        state.setAnim("orb_fall");
                    }
                }
            } else {
                if (state.isCurrentAnim("orb_spin") || state.isCurrentAnim("orb_rise")) {
                    state.setAnim("orb_fall");
                } else if ((state.isCurrentAnim("idle_exciters_out")) || ((state.isCurrentAnim("extend_exciters") || state.isCurrentAnim("orb_fall")) && state.isDone())) {
                    state.setAnim("retract_exciters");
                }
            }
            animate(pEntity.animState);
        }

        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("charger"));
            }
            return model;
        }
    }
}