package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.processing.EntropicProcessor;
import com.cmdpro.datanessence.block.technical.cryochamber.CryochamberBlockEntity;
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

public class CryochamberRenderer extends DatabankBlockEntityRenderer<CryochamberBlockEntity> {
    public CryochamberRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    @Override
    public void render(CryochamberBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0, 0.25, 0);
        Vec3 rotateAround = new Vec3(0.5, 0.5, 0.5);
        Direction facing = pBlockEntity.getBlockState().getValue(EntropicProcessor.FACING);
        if (facing.equals(Direction.NORTH))
            pPoseStack.rotateAround(Axis.YP.rotationDegrees(0), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.SOUTH))
            pPoseStack.rotateAround(Axis.YP.rotationDegrees(180), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.EAST))
            pPoseStack.rotateAround(Axis.YP.rotationDegrees(-90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.WEST))
            pPoseStack.rotateAround(Axis.YP.rotationDegrees(90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);

        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        pPoseStack.popPose();
    }


    public static class Model extends DatabankBlockEntityModel<CryochamberBlockEntity> {
        public DatabankModel model;
        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/dead_makutuin.png");
        }

        @Override
        public void setupModelPose(CryochamberBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            animate(pEntity.animState);
        }

        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("dead_makutuin"));
            }
            return model;
        }
    }
}
