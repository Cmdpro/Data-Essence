package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
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
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(DataNEssence.locate("dead_makutuin"), "main");

    public ArekkoRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return DataNEssence.locate("textures/block/dead_makutuin.png");
    }

    public static class Model extends DatabankBlockEntityModel<ArekkoBlockEntity> {
        public static DatabankEntityModel model;
        public static AnimationDefinition idle;
        private final ModelPart root;

        public Model(ModelPart root) {
            this.root = root.getChild("root");
        }

        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("dead_makutuin"));
                idle = model.animations.get("idle").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(ArekkoBlockEntity pEntity) {
            pEntity.animState.startIfStopped((int)getAgeInTicks());
            this.animate(pEntity.animState, idle, 1.0f);
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}
