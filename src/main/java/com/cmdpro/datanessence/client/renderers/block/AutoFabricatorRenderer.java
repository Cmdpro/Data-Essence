package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.processing.AutoFabricatorBlockEntity;
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

public class AutoFabricatorRenderer extends DatabankBlockEntityRenderer<AutoFabricatorBlockEntity> {
    public AutoFabricatorRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    @Override
    public void render(AutoFabricatorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 0.5D, 0.5D);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getLevel().getLevelData().getGameTime() % 360));
        pPoseStack.scale(0.25F, 0.25F, 0.25F);
        Minecraft.getInstance().getItemRenderer().renderStatic(pBlockEntity.item, ItemDisplayContext.GUI, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pPoseStack, MachineOutputShader.createMachineOutputBufferSource(), pBlockEntity.getLevel(), 0);
        pPoseStack.popPose();
    }

    public static class Model extends DatabankBlockEntityModel<AutoFabricatorBlockEntity> {
        public DatabankModel model;

        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/auto-fabricator.png");
        }

        @Override
        public void setupModelPose(AutoFabricatorBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            if (pEntity.craftingProgress >= 0) {
                pEntity.animState.setAnim("crafting");
            } else {
                pEntity.animState.setAnim("idle");
            }
            animate(pEntity.animState);
        }

        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("auto-fabricator"));
            }
            return model;
        }
    }
}