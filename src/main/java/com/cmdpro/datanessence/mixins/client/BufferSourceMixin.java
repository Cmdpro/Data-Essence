package com.cmdpro.datanessence.mixins.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.SequencedMap;

@Mixin(MultiBufferSource.BufferSource.class)
public interface BufferSourceMixin {
    @Accessor("sharedBuffer")
    public ByteBufferBuilder getSharedBuffer();

    @Accessor("fixedBuffers")
    public SequencedMap<RenderType, ByteBufferBuilder> getFixedBuffers();
}
