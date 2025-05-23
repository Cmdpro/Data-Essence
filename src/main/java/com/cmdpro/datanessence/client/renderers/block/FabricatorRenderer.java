package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.processing.FabricatorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class FabricatorRenderer extends DatabankBlockEntityRenderer<FabricatorBlockEntity> {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(DataNEssence.locate("fabricator"), "main");
    public FabricatorRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public void render(FabricatorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 1.5D, 0.5D);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getLevel().getLevelData().getGameTime() % 360));
        pPoseStack.scale(0.75F, 0.75F, 0.75F);
        Minecraft.getInstance().getItemRenderer().renderStatic(pBlockEntity.item, ItemDisplayContext.GUI, pPackedLight, pPackedOverlay, pPoseStack, pBufferSource, pBlockEntity.getLevel(), 0);
        pPoseStack.popPose();
    }
    @Override
    public ResourceLocation getTextureLocation() {
        return DataNEssence.locate("textures/block/fabricator.png");
    }

    public static class Model extends DatabankBlockEntityModel<FabricatorBlockEntity> {
        public static AnimationDefinition idle;
        public static AnimationDefinition ready;
        private final ModelPart root;

        public Model(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
        }
        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("fabricator"));
                idle = model.animations.get("idle").createAnimationDefinition();
                ready = model.animations.get("ready").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(FabricatorBlockEntity pEntity) {
            pEntity.animState.startIfStopped((int)getAgeInTicks());
            if (!pEntity.item.isEmpty()) {
                this.animate(pEntity.animState, ready, 1.0f);
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