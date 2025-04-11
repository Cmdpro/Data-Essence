package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.datanessence.api.item.ILaserEmitterModule;
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
    public void render(LaserEmitterBlockEntity laserEmitter, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (laserEmitter.end != null) {
            Vec3 pos = laserEmitter.getBlockPos().getCenter();
            poseStack.pushPose();
            poseStack.translate(-pos.x, -pos.y, -pos.z);
            poseStack.translate(0.5, 0.5, 0.5);

            int color = 0xe236ef;
            if ( laserEmitter.item.getItem() instanceof ILaserEmitterModule lens )
                color = lens.getBeamColor();

            ClientDatabankUtils.renderAdvancedBeaconBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, partialTick, 1.0f, laserEmitter.getLevel().getGameTime(), laserEmitter.getBlockPos().getCenter(), laserEmitter.end, new Color(color), 0.25f, 0.3f);
            poseStack.popPose();
        }
    }

    @Override
    public AABB getRenderBoundingBox(LaserEmitterBlockEntity laserEmitter) {
        if (laserEmitter.end != null) {
            AABB bounds = AABB.encapsulatingFullBlocks(laserEmitter.getBlockPos(), BlockPos.containing(laserEmitter.end));
            return bounds;
        } else {
            return BlockEntityRenderer.super.getRenderBoundingBox(laserEmitter);
        }
    }

    EntityRenderDispatcher renderDispatcher;
    public LaserEmitterRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}
