package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.datanessence.api.node.block.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.block.transmission.StructuralPointBlockEntity;
import com.cmdpro.datanessence.client.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class StructuralPointRenderer implements BlockEntityRenderer<StructuralPointBlockEntity> {

    public StructuralPointRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(StructuralPointBlockEntity be,
                       float partialTick,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay) {

        if (be.link == null || be.link.isEmpty()) {
            return;
        }

        BlockPos blockPos = be.getBlockPos();
        Vec3 origin = blockPos.getCenter();

        poseStack.pushPose();
        // move world-space origin to block center
        poseStack.translate(-origin.x, -origin.y, -origin.z);
        poseStack.translate(0.5, 0.5, 0.5);

        Color segColor1 = be.linkColor();
        float darken = 1.5f;
        Color segColor2 = new Color(
                (int) (segColor1.getRed() / darken),
                (int) (segColor1.getGreen() / darken),
                (int) (segColor1.getBlue() / darken),
                segColor1.getAlpha()
        );
        Color segColor3 = ColorUtil.blendColors(segColor1, segColor2, 0.35f);

        float ticks = (Minecraft.getInstance().level.getGameTime() % 8) + partialTick;
        int currentSeg = (int) (ticks % 8);

        for (BlockPos targetPos : be.link) {
            Vec3 target = targetPos.getCenter();
            VertexConsumer vc = RenderHandler.createBufferSource()
                    .getBuffer(DataNEssenceRenderTypes.WIRES);

            boolean straightVertical = blockPos.getX() == targetPos.getX()
                    && blockPos.getZ() == targetPos.getZ();

            ClientRenderingUtil.renderLine(vc, poseStack, origin, target, seg -> {
                int invSeg = 7 - (seg % 8);
                int offM2 = getSegWithOffset(currentSeg, -2);
                int offP1 = getSegWithOffset(currentSeg, 1);
                int offM1 = getSegWithOffset(currentSeg, -1);

                if (invSeg == offM2 || invSeg == offP1) {
                    return segColor3;
                }
                return (invSeg == currentSeg || invSeg == offM1) ? segColor1 : segColor2;
            }, straightVertical ? 0.0 : 0.3);
        }

        poseStack.popPose();
    }

    private static int getSegWithOffset(int seg, int offset) {
        int s = seg + offset;
        if (s < 0) s += 8;
        return s % 8;
    }

    @Override
    public AABB getRenderBoundingBox(StructuralPointBlockEntity entity) {
        return AABB.INFINITE;
    }

    @Override
    public boolean shouldRenderOffScreen(StructuralPointBlockEntity entity) {
        return true;
    }
}
