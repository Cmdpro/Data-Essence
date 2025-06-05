package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.pearlnetwork.PearlNetworkBlockRenderHelper;
import com.cmdpro.datanessence.block.transportation.EnderPearlCaptureBlockEntity;
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

public class EnderPearlRelayRenderer extends DatabankBlockEntityRenderer<EnderPearlRelayBlockEntity> implements PearlNetworkBlockRenderHelper {
    public EnderPearlRelayRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    @Override
    public void render(EnderPearlRelayBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        renderPearlConnections(pBlockEntity, pPoseStack);
    }
    @Override
    public AABB getRenderBoundingBox(EnderPearlRelayBlockEntity blockEntity) {
        return AABB.INFINITE;
    }

    public static class Model extends DatabankBlockEntityModel<EnderPearlRelayBlockEntity> {
        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/ender_pearl_relay.png");
        }

        @Override
        public void setupModelPose(EnderPearlRelayBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            animate(pEntity.animState);
        }

        public DatabankModel model;
        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("ender_pearl_relay"));
            }
            return model;
        }
    }
}