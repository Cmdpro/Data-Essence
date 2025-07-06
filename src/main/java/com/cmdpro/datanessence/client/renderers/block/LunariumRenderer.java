package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.processing.FabricatorBlockEntity;
import com.cmdpro.datanessence.block.processing.LunariumBlockEntity;
import com.cmdpro.datanessence.client.shaders.MachineOutputShader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class LunariumRenderer extends DatabankBlockEntityRenderer<LunariumBlockEntity> {
    public LunariumRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    @Override
    public void render(LunariumBlockEntity lunarium, float pTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        super.render(lunarium, pTick, poseStack, buffers, packedLight, packedOverlay);
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(lunarium.getLevel().getLevelData().getGameTime() % 360));
        poseStack.scale(0.75F, 0.75F, 0.75F);
        Minecraft.getInstance().getItemRenderer().renderStatic(lunarium.item, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, poseStack, MachineOutputShader.createMachineOutputBufferSource(), lunarium.getLevel(), 0);
        poseStack.popPose();
    }

    public static class Model extends DatabankBlockEntityModel<LunariumBlockEntity> {
        public DatabankModel model;
        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("lunarium"));
            }
            return model;
        }
        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/lunarium.png");
        }

        @Override
        public void setupModelPose(LunariumBlockEntity lunarium, float pTick) {
            lunarium.animState.updateAnimDefinitions(getModel());
            lunarium.animState.setAnim("pose");
            animate(lunarium.animState);
        }
    }
}