package com.cmdpro.datanessence.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import com.cmdpro.datanessence.block.auxiliary.EnticingLureBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class EnticingLureRenderer implements BlockEntityRenderer<EnticingLureBlockEntity> {

    @Override
    public void render(EnticingLureBlockEntity lure, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack item = lure.getItemHandler().getStackInSlot(0);

        poseStack.pushPose();
        poseStack.translate(0.5d, 1.0d, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(lure.getLevel().getLevelData().getGameTime() % 360)); // TODO rotate like the ChC item fabricator
        poseStack.scale(0.25F, 0.25F, 0.25F);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GUI, packedLight, packedOverlay, poseStack, bufferSource, lure.getLevel(), 0);
        poseStack.popPose();
    }

    EntityRenderDispatcher renderDispatcher;
    public EnticingLureRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}
