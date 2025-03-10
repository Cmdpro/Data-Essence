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

public class MoteParticleOptions implements ParticleOptions {
    public Color color;
    public boolean additive;
    public int lifetime;

    public MoteParticleOptions(Color color, boolean additive) {
        this.color = color;
        this.additive = additive;
        this.lifetime = 20;
    }

    public MoteParticleOptions(Color color, boolean additive, int lifetime) {
        this.color = color;
        this.additive = additive;
        this.lifetime = lifetime;
    }

    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.MOTE.get();
    }

    public static final MapCodec<MoteParticleOptions> CODEC = RecordCodecBuilder.mapCodec((builder) -> {
        return builder.group(ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("color").forGetter((particle) -> {
            return particle.color.getRGB();
        }), Codec.BOOL.fieldOf("additive").forGetter((particle) -> {
            return particle.additive;
        }), Codec.INT.fieldOf("lifetime").forGetter((particle) -> {
            return particle.lifetime;
        })).apply(builder, (color, additive, lifetime) -> new MoteParticleOptions(new Color(color), additive, lifetime));
    });


    public static final StreamCodec<RegistryFriendlyByteBuf, MoteParticleOptions> STREAM_CODEC = StreamCodec.of((buf, options) -> {
        buf.writeInt(options.color.getRed());
        buf.writeInt(options.color.getGreen());
        buf.writeInt(options.color.getBlue());
        buf.writeInt(options.color.getAlpha());
        buf.writeBoolean(options.additive);
        buf.writeInt(options.lifetime);
    }, (buf) -> {
        int r = buf.readInt();
        int g = buf.readInt();
        int b = buf.readInt();
        int a = buf.readInt();
        boolean additive = buf.readBoolean();
        int lifetime = buf.readInt();
        return new MoteParticleOptions(new Color(r, g, b, a), additive, lifetime);
    });
}