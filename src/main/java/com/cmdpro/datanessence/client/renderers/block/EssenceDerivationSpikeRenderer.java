package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.generation.EssenceDerivationSpikeBlockEntity;

import com.cmdpro.datanessence.block.transportation.EnderPearlRelayBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class EssenceDerivationSpikeRenderer extends DatabankBlockEntityRenderer<EssenceDerivationSpikeBlockEntity> {
    public EssenceDerivationSpikeRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    public static class Model extends DatabankBlockEntityModel<EssenceDerivationSpikeBlockEntity> {
        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/essence_derivation_spike.png");
        }

        @Override
        public void setupModelPose(EssenceDerivationSpikeBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            if ( (pEntity.hasRedstone && pEntity.hasStructure && !pEntity.isBroken) && pEntity.animState.isCurrentAnim("idle") )
                pEntity.animState.setAnim("extend_spike");
            if ( (!pEntity.hasRedstone || !pEntity.hasStructure || pEntity.isBroken) && pEntity.animState.isCurrentAnim("rotate_rings") )
                pEntity.animState.setAnim("retract_spike");
            animate(pEntity.animState);
        }

        public DatabankModel model;
        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("essence_derivation_spike"));
            }
            return model;
        }
    }
}
