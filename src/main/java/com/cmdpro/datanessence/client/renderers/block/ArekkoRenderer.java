package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.technical.ArekkoBlockEntity;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ArekkoRenderer extends DatabankBlockEntityRenderer<ArekkoBlockEntity> {
    public ArekkoRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    public static class Model extends DatabankBlockEntityModel<ArekkoBlockEntity> {
        public DatabankModel model;
        public static AnimationDefinition idle;

        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/dead_makutuin.png");
        }

        @Override
        public void setupModelPose(ArekkoBlockEntity pEntity, float partialTick) {
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
