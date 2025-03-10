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

public class CircleParticleOptions implements ParticleOptions {
    public Color color;
    public CircleParticleOptions(Color color) {
        this.color = color;
    }
    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.CIRCLE.get();
    }

    public static final MapCodec<CircleParticleOptions> CODEC = RecordCodecBuilder.mapCodec((builder) -> {
        return builder.group(ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("color").forGetter((particle) -> {
            return particle.color.getRGB();
        })).apply(builder, (color) -> new CircleParticleOptions(new Color(color)));
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, CircleParticleOptions> STREAM_CODEC = StreamCodec.of((buf, options) -> {
        buf.writeInt(options.color.getRed());
        buf.writeInt(options.color.getGreen());
        buf.writeInt(options.color.getBlue());
        buf.writeInt(options.color.getAlpha());
    }, (buf) -> {
        int r = buf.readInt();
        int g = buf.readInt();
        int b = buf.readInt();
        int a = buf.readInt();
        return new CircleParticleOptions(new Color(r, g, b, a));
    });
}