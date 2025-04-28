package com.cmdpro.datanessence.client.particle;

import com.cmdpro.datanessence.registry.ParticleRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.awt.*;

public class RhombusParticleOptions extends ConfigurableParticleOptions implements ParticleOptions {
    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.RHOMBUS.get();
    }
    public static final MapCodec<RhombusParticleOptions> CODEC = createCodec(RhombusParticleOptions::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, RhombusParticleOptions> STREAM_CODEC = createStreamCodec(RhombusParticleOptions::new);
}