package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.generation.derivationspike.EssenceDerivationSpikeBlockEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class EssenceDerivationSpikeRenderer extends DatabankBlockEntityRenderer<EssenceDerivationSpikeBlockEntity> {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(DataNEssence.locate("essence_derivation_spike"), "main");

    public EssenceDerivationSpikeRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return DataNEssence.locate("textures/block/essence_derivation_spike.png");
    }

    public static class Model extends DatabankBlockEntityModel<EssenceDerivationSpikeBlockEntity> {
        public static DatabankEntityModel model;
        private final ModelPart root;

        public Model(ModelPart root) {
            this.root = root.getChild("root");
        }

        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("essence_derivation_spike"));
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }

        public void setupAnim(EssenceDerivationSpikeBlockEntity spike) {
            DatabankAnimationState state = spike.animState;

            if ( (spike.hasRedstone && spike.hasStructure && !spike.isBroken) && state.isCurrentAnim("idle") )
                state.setAnim("extend_spike");
            if ( (!spike.hasRedstone || !spike.hasStructure || spike.isBroken) && state.isCurrentAnim("rotate_rings") )
                state.setAnim("retract_spike");

            state.updateAnimDefinitions(model);
            state.update();
            this.animate(state.state, state.getAnim().definition);
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}
