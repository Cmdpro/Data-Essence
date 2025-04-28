package com.cmdpro.datanessence.client.particle;

import com.cmdpro.datanessence.registry.ParticleRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.awt.*;

public class CircleParticleOptions extends ConfigurableParticleOptions implements ParticleOptions {
    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.CIRCLE.get();
    }
    public static final MapCodec<CircleParticleOptions> CODEC = createCodec(CircleParticleOptions::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, CircleParticleOptions> STREAM_CODEC = createStreamCodec(CircleParticleOptions::new);
}