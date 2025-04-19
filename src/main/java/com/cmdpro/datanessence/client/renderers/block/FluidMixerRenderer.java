package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
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
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(DataNEssence.locate("fluid_mixer"), "main");

    public FluidMixerRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new FluidMixerRenderer.Model(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return DataNEssence.locate("textures/block/fluid_mixer.png");
    }

    public static class Model extends DatabankBlockEntityModel<FluidMixerBlockEntity> {
        public static DatabankEntityModel model;
        public static AnimationDefinition idle;
        public static AnimationDefinition working;
        private final ModelPart root;

        public Model(ModelPart root) {
            this.root = root.getChild("root");
        }

        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("fluid_mixer"));
                idle = model.animations.get("idle").createAnimationDefinition();
                working = model.animations.get("working").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(FluidMixerBlockEntity pEntity) {
            pEntity.animState.startIfStopped((int)getAgeInTicks());
            if (pEntity.workTime >= 0) {
                this.animate(pEntity.animState, working, 1.0f);
            } else {
                this.animate(pEntity.animState, idle, 1.0f);
            }
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}
