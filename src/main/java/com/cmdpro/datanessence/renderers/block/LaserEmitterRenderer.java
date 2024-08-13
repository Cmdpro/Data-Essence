package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.block.auxiliary.LaserEmitterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class LaserEmitterRenderer implements BlockEntityRenderer<LaserEmitterBlockEntity> {
    @Override
    public void render(LaserEmitterBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.end != null) {
            Vec3 pos = pBlockEntity.getBlockPos().getCenter();
            pPoseStack.pushPose();
            pPoseStack.translate(-pos.x, -pos.y, -pos.z);
            pPoseStack.translate(0.5, 0.5, 0.5);
            ClientRenderingUtil.renderBeam(pPoseStack, pBuffer, BeaconRenderer.BEAM_LOCATION, pPartialTick, 1.0f, pBlockEntity.getLevel().getGameTime(), pBlockEntity.getBlockPos().getCenter(), pBlockEntity.end, new Color(0xe236ef), 0.25f, 0.3f);
            pPoseStack.popPose();
        }
    }

    @Override
    public AABB getRenderBoundingBox(LaserEmitterBlockEntity blockEntity) {
        if (blockEntity.end != null) {
            AABB bounds = AABB.encapsulatingFullBlocks(blockEntity.getBlockPos(), BlockPos.containing(blockEntity.end));
            return bounds;
        } else {
            return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
        }
    }

    EntityRenderDispatcher renderDispatcher;
    public LaserEmitterRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}
