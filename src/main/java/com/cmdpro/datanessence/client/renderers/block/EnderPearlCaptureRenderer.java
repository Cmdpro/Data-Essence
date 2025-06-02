package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.pearlnetwork.PearlNetworkBlockRenderHelper;
import com.cmdpro.datanessence.block.processing.AutoFabricatorBlockEntity;
import com.cmdpro.datanessence.block.transportation.EnderPearlCaptureBlockEntity;
import com.cmdpro.datanessence.client.shaders.MachineOutputShader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class EnderPearlCaptureRenderer extends DatabankBlockEntityRenderer<EnderPearlCaptureBlockEntity> implements PearlNetworkBlockRenderHelper {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(DataNEssence.locate("ender_pearl_capture"), "main");
    public EnderPearlCaptureRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public void render(EnderPearlCaptureBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        renderPearlConnections(pBlockEntity, pPoseStack);
    }
    @Override
    public ResourceLocation getTextureLocation() {
        return DataNEssence.locate("textures/block/ender_pearl_capture.png");
    }

    public static class Model extends DatabankBlockEntityModel<EnderPearlCaptureBlockEntity> {
        public static AnimationDefinition idle;
        public static AnimationDefinition crafting;
        private final ModelPart root;

        public Model(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
        }
        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("ender_pearl_capture"));
                idle = model.animations.get("idle").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(EnderPearlCaptureBlockEntity pEntity) {
            pEntity.animState.startIfStopped((int)getAgeInTicks());
            this.animate(pEntity.animState, idle, 1.0f);
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}