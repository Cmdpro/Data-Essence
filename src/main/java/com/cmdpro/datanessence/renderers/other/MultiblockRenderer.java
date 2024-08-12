package com.cmdpro.datanessence.renderers.other;

import com.cmdpro.datanessence.DataNEssence;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.SequencedMap;

public class MultiblockRenderer {
    public static MultiBufferSource.BufferSource buffers;
    public static void renderBlock(BlockState block, BlockPos pos, PoseStack stack) {
        if (buffers == null) {
            buffers = initBuffers(Minecraft.getInstance().renderBuffers().bufferSource());
        }
        renderBlock(block, pos, stack, buffers);
    }
    public static void renderBlock(BlockState block, BlockPos pos, PoseStack stack, MultiBufferSource.BufferSource bufferSource) {
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        FluidState fluidState = block.getFluidState();
        if (!fluidState.isEmpty()) {
            RenderType layer = ItemBlockRenderTypes.getRenderLayer(fluidState);
            VertexConsumer buffer = bufferSource.getBuffer(layer);
            blockRenderer.renderLiquid(pos, Minecraft.getInstance().level, buffer, block, fluidState);
        }
        if (block.getRenderShape() != RenderShape.INVISIBLE) {
            BakedModel model = blockRenderer.getBlockModel(block);
            for (RenderType i : model.getRenderTypes(block, Minecraft.getInstance().level.random, ModelData.EMPTY)) {
                VertexConsumer hologramConsumer = bufferSource.getBuffer(i);
                blockRenderer.renderBatched(block, pos, Minecraft.getInstance().level, stack, hologramConsumer, false, Minecraft.getInstance().level.random, ModelData.EMPTY, i);
            }
        }
    }
    private static MultiBufferSource.BufferSource initBuffers(MultiBufferSource.BufferSource original) {
        var fallback = original.sharedBuffer;
        var layerBuffers = original.fixedBuffers;
        SequencedMap<RenderType, ByteBufferBuilder> remapped = new Object2ObjectLinkedOpenHashMap<>();
        for (Map.Entry<RenderType, ByteBufferBuilder> e : layerBuffers.entrySet()) {
            remapped.put(HologramRenderLayer.remap(e.getKey()), e.getValue());
        }
        return new HologramBuffers(fallback, remapped);
    }
    private static class HologramBuffers extends MultiBufferSource.BufferSource {
        protected HologramBuffers(ByteBufferBuilder fallback, SequencedMap<RenderType, ByteBufferBuilder> layerBuffers) {
            super(fallback, layerBuffers);
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            return super.getBuffer(HologramRenderLayer.remap(type));
        }
    }

    private static class HologramRenderLayer extends RenderType {
        private static final Map<RenderType, RenderType> remappedTypes = new IdentityHashMap<>();

        private HologramRenderLayer(RenderType original) {
            super(String.format("%s_%s_hologram", original.toString(), DataNEssence.MOD_ID), original.format(), original.mode(), original.bufferSize(), original.affectsCrumbling(), true, () -> {
                original.setupRenderState();

                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(0.25f, 0.25f, 1, 0.4F);
            }, () -> {
                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();

                original.clearRenderState();
            });
        }

        public static RenderType remap(RenderType in) {
            if (in instanceof HologramRenderLayer) {
                return in;
            } else {
                return remappedTypes.computeIfAbsent(in, HologramRenderLayer::new);
            }
        }
    }
}
