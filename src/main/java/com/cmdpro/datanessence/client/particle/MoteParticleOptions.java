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

public class MoteParticleOptions extends ConfigurableParticleOptions implements ParticleOptions {
    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.MOTE.get();
    }
    public static final MapCodec<MoteParticleOptions> CODEC = createCodec(MoteParticleOptions::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, MoteParticleOptions> STREAM_CODEC = createStreamCodec(MoteParticleOptions::new);
}