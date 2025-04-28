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

public class SmallCircleParticleOptions extends ConfigurableParticleOptions implements ParticleOptions {
    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.SMALL_CIRCLE.get();
    }
    public static final MapCodec<SmallCircleParticleOptions> CODEC = createCodec(SmallCircleParticleOptions::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, SmallCircleParticleOptions> STREAM_CODEC = createStreamCodec(SmallCircleParticleOptions::new);
}