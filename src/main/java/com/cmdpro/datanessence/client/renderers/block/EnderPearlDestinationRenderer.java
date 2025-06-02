package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.pearlnetwork.PearlNetworkBlockRenderHelper;
import com.cmdpro.datanessence.block.transportation.EnderPearlCaptureBlockEntity;
import com.cmdpro.datanessence.block.transportation.EnderPearlDestinationBlockEntity;
import com.cmdpro.datanessence.block.transportation.EnderPearlRelayBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public class EnderPearlDestinationRenderer extends DatabankBlockEntityRenderer<EnderPearlDestinationBlockEntity> implements PearlNetworkBlockRenderHelper {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(DataNEssence.locate("ender_pearl_destination"), "main");
    public EnderPearlDestinationRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public void render(EnderPearlDestinationBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        renderPearlConnections(pBlockEntity, pPoseStack);
    }
    @Override
    public ResourceLocation getTextureLocation() {
        return DataNEssence.locate("textures/block/ender_pearl_destination.png");
    }
    @Override
    public AABB getRenderBoundingBox(EnderPearlDestinationBlockEntity blockEntity) {
        return AABB.INFINITE;
    }

    public static class Model extends DatabankBlockEntityModel<EnderPearlDestinationBlockEntity> {
        public static AnimationDefinition idle;
        private final ModelPart root;

        public Model(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
        }
        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("ender_pearl_destination"));
                idle = model.animations.get("idle").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(EnderPearlDestinationBlockEntity pEntity) {
            pEntity.animState.startIfStopped((int)getAgeInTicks());
            this.animate(pEntity.animState, idle, 1.0f);
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}