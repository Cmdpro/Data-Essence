package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.auxiliary.SurveyTunneler;
import com.cmdpro.datanessence.block.auxiliary.SurveyTunnelerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class SurveyTunnelerRenderer extends DatabankBlockEntityRenderer<SurveyTunnelerBlockEntity>{
    public SurveyTunnelerRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    @Override
    public void render(SurveyTunnelerBlockEntity tunneler, float pPartialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Direction facing = tunneler.getBlockState().getValue(SurveyTunneler.FACING);
        Vec3 rotateAround = new Vec3(0.5, 0.5, 0.5);

        if (facing.equals(Direction.NORTH))
            poseStack.rotateAround(Axis.YP.rotationDegrees(0), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.SOUTH))
            poseStack.rotateAround(Axis.YP.rotationDegrees(180), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.EAST))
            poseStack.rotateAround(Axis.YP.rotationDegrees(-90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.WEST))
            poseStack.rotateAround(Axis.YP.rotationDegrees(90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);

        super.render(tunneler, pPartialTick, poseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }


    public static class Model extends DatabankBlockEntityModel<SurveyTunnelerBlockEntity> {
        private DatabankModel model;

        @Override
        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("survey_tunneler"));
            }
            return model;
        }

        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/survey_tunneler.png");
        }

        @Override
        public void setupModelPose(SurveyTunnelerBlockEntity tunneler, float partialTick) {
            DatabankModel databankModel = getModel();
            if (databankModel == null) {
                return;
            }

            tunneler.animState.updateAnimDefinitions(databankModel);
            animate(tunneler.animState);
        }

    }



}
