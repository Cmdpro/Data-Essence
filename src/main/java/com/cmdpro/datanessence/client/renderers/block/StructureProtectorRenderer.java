package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.megastructures.block.MegastructureSaveBlockEntity;
import com.cmdpro.datanessence.block.technical.StructureProtectorBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class StructureProtectorRenderer implements BlockEntityRenderer<StructureProtectorBlockEntity> {
    EntityRenderDispatcher renderDispatcher;
    public StructureProtectorRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
    @Override
    public void render(StructureProtectorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (Minecraft.getInstance().player.canUseGameMasterBlocks()) {
            BlockPos corner1 = blockEntity.getCorner1();
            BlockPos corner2 = blockEntity.getCorner2();
            BlockPos center = blockEntity.getBlockPos();
            Color color = Color.GREEN;
            Color centerColor = Color.WHITE;
            boolean complete = true;
            if (corner1 == null) {
                color = Color.RED;
                centerColor = Color.YELLOW;
                complete = false;
            } else if (corner2 == null) {
                corner2 = corner1;
                color = Color.RED;
                centerColor = Color.YELLOW;
                complete = false;
            }
            Vec3 renderOffset = blockEntity.getBlockPos().getCenter().subtract(0.5f, 0.5f, 0.5f).scale(-1);
            VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            if (corner1 != null && corner2 != null) {
                BlockPos minBlock = new BlockPos(
                        Math.min(corner1.getX(), corner2.getX()),
                        Math.min(corner1.getY(), corner2.getY()),
                        Math.min(corner1.getZ(), corner2.getZ())
                );
                BlockPos maxBlock = new BlockPos(
                        Math.max(corner1.getX(), corner2.getX()),
                        Math.max(corner1.getY(), corner2.getY()),
                        Math.max(corner1.getZ(), corner2.getZ())
                );
                Vec3 min = minBlock.getCenter().add(-0.5f, -0.5f, -0.5f).add(renderOffset);
                Vec3 max = maxBlock.getCenter().add(0.5f, 0.5f, 0.5f).add(renderOffset);
                LevelRenderer.renderLineBox(poseStack, vertexconsumer, min.x, min.y, min.z, max.x, max.y, max.z, (float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, 1.0F, ((float) color.getRed() / 255f) / 2f, ((float) color.getGreen() / 255f) / 2f, ((float) color.getBlue() / 255f) / 2f);
            }
            if (!complete) {
                Vec3 centerMin = center.getCenter().add(-0.5f, -0.5f, -0.5f).add(renderOffset);
                Vec3 centerMax = center.getCenter().add(0.5f, 0.5f, 0.5f).add(renderOffset);
                LevelRenderer.renderLineBox(poseStack, vertexconsumer, centerMin.x, centerMin.y, centerMin.z, centerMax.x, centerMax.y, centerMax.z, (float) centerColor.getRed() / 255f, (float) centerColor.getGreen() / 255f, (float) centerColor.getBlue() / 255f, 1.0F, ((float) centerColor.getRed() / 255f) / 2f, ((float) centerColor.getGreen() / 255f) / 2f, ((float) centerColor.getBlue() / 255f) / 2f);
            }
            if (bufferSource instanceof MultiBufferSource.BufferSource source) {
                source.endBatch(RenderType.lines());
            }
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
        }
    }
}
