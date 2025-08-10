package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.api.item.EssenceShard;
import com.cmdpro.datanessence.block.auxiliary.EssenceReaderBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.Vec3;

public class EssenceReaderRenderer implements BlockEntityRenderer<EssenceReaderBlockEntity> {

    @Override
    public void render(EssenceReaderBlockEntity reader, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        EssenceShard shard = reader.selectedShard;
        if (shard != null) {
            // render the shard floating above the reader
        }
    }

    @Override
    public boolean shouldRender(EssenceReaderBlockEntity reader, Vec3 cameraPos) {
        return BlockEntityRenderer.super.shouldRender(reader, cameraPos);
        // render only if player is looking at block
    }
}
