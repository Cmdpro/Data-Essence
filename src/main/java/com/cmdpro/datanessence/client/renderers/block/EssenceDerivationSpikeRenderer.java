package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.generation.derivationspike.EssenceDerivationSpikeBlockEntity;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;

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
        public static AnimationDefinition rotateRings;
        public static AnimationDefinition extendSpike;
        public static AnimationDefinition retractSpike;
        private final ModelPart root;

        public Model(ModelPart root) {
            this.root = root.getChild("root");
        }

        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("essence_derivation_spike"));
                rotateRings = model.animations.get("rotate_rings").createAnimationDefinition();
                extendSpike = model.animations.get("extend_spike").createAnimationDefinition();
                retractSpike = model.animations.get("retract_spike").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }

        public void setupAnim(EssenceDerivationSpikeBlockEntity spike) {
            spike.animState.startIfStopped((int) getAgeInTicks());
            AnimationDefinition anim;

            // TODO extendSpike needs to play, fix animations

            if ( spike.hasRedstone && spike.hasStructure && !spike.isBroken )
                anim = rotateRings;
            else
                anim = retractSpike;

            if ( anim != spike.anim )
                updateAnim(spike, spike.animState, anim);

            this.animate(spike.animState, anim, 1.0f);
        }

        protected void updateAnim(EssenceDerivationSpikeBlockEntity entity, AnimationState state, AnimationDefinition definition) {
            if (entity.anim != definition) {
                state.stop();
                state.start((int)getAgeInTicks());
                entity.anim = definition;
            }
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}
