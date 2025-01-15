package com.cmdpro.datanessence.renderers.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import com.cmdpro.datanessence.block.auxiliary.EnticingLureBlockEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class EnticingLureRenderer implements BlockEntityRenderer<EnticingLureBlockEntity> {

    @Override
    public void render(EnticingLureBlockEntity lure, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack item = lure.item;

        // TODO item does not render

        poseStack.pushPose();
        poseStack.translate(0.5d, 0.8d, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(lure.getLevel().getLevelData().getGameTime() % 360));
        poseStack.scale(0.25F, 0.25F, 0.25F);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GUI, packedLight, packedOverlay, poseStack, bufferSource, lure.getLevel(), 0);
        poseStack.popPose();
    }
}
