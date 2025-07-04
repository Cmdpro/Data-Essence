package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.processing.EntropicProcessor;
import com.cmdpro.datanessence.block.processing.EntropicProcessorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class EntropicProcessorRenderer extends DatabankBlockEntityRenderer<EntropicProcessorBlockEntity>{
    public EntropicProcessorRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    @Override
    public void render(EntropicProcessorBlockEntity entropicProcessor, float pPartialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Direction facing = entropicProcessor.getBlockState().getValue(EntropicProcessor.FACING);
        Vec3 rotateAround = new Vec3(0.5, 0.5, 0.5);

        if (facing.equals(Direction.NORTH))
            poseStack.rotateAround(Axis.YP.rotationDegrees(0), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.SOUTH))
            poseStack.rotateAround(Axis.YP.rotationDegrees(180), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.EAST))
            poseStack.rotateAround(Axis.YP.rotationDegrees(-90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.WEST))
            poseStack.rotateAround(Axis.YP.rotationDegrees(90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);

        super.render(entropicProcessor, pPartialTick, poseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }


    public static class Model extends DatabankBlockEntityModel<EntropicProcessorBlockEntity> {
        public DatabankModel model;
        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("entropic_processor"));
            }
            return model;
        }
        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/entropic_processor.png");
        }

        @Override
        public void setupModelPose(EntropicProcessorBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            if (pEntity.workTime >= 0) {
                pEntity.animState.setAnim("working");
            } else {
                pEntity.animState.setAnim("idle");
            }
            animate(pEntity.animState);
        }
    }
}
