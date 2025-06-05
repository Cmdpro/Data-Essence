package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.processing.FluidMixerBlockEntity;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class FluidMixerRenderer extends DatabankBlockEntityRenderer<FluidMixerBlockEntity> {
    public FluidMixerRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new FluidMixerRenderer.Model());
    }

    public static class Model extends DatabankBlockEntityModel<FluidMixerBlockEntity> {
        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/fluid_mixer.png");
        }

        @Override
        public void setupModelPose(FluidMixerBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            if (pEntity.workTime >= 0) {
                pEntity.animState.setAnim("working");
            } else {
                pEntity.animState.setAnim("idle");
            }
            animate(pEntity.animState);
        }

        public DatabankModel model;
        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("fluid_mixer"));
            }
            return model;
        }
    }
}
