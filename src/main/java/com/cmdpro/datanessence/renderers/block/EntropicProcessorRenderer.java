package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.processing.EntropicProcessorBlockEntity;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;

public class EntropicProcessorRenderer extends DatabankBlockEntityRenderer<EntropicProcessorBlockEntity>{
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "entropic_processor"), "main");

    public EntropicProcessorRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/entropic_processor.png");
    }

    public static class Model extends DatabankBlockEntityModel<EntropicProcessorBlockEntity> {
        public static DatabankEntityModel model;
        public static AnimationDefinition idle;
        public static AnimationDefinition working;
        public static final AnimationState state = new AnimationState();
        private final ModelPart root;

        public Model(ModelPart root) {
            this.root = root.getChild("root");
        }

        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "entropic_processor"));
                idle = model.animations.get("idle").createAnimationDefinition();
                working = model.animations.get("working").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(EntropicProcessorBlockEntity pEntity) {
            state.startIfStopped((int)getAgeInTicks());
            if (pEntity.workTime >= 0) {
                this.animate(state, working, 1.0f);
            } else {
                this.animate(state, idle, 1.0f);
            }
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}
