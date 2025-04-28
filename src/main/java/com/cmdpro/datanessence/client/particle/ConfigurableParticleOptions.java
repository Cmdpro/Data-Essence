package com.cmdpro.datanessence.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.awt.*;
import java.util.function.Supplier;

public abstract class ConfigurableParticleOptions implements ParticleOptions {
    public ConfigurableParticleOptions() {}
    public Color color = Color.WHITE;
    public boolean additive = false;
    public int lifetime = 20;
    public float friction = 0.8f;
    public float gravity = 0f;
    public ConfigurableParticleOptions setColor(Color color) {
        this.color = color;
        return this;
    }
    public ConfigurableParticleOptions setAdditive(boolean additive) {
        this.additive = additive;
        return this;
    }
    public ConfigurableParticleOptions setLifetime(int lifetime) {
        this.lifetime = lifetime;
        return this;
    }
    public ConfigurableParticleOptions setFriction(float friction) {
        this.friction = friction;
        return this;
    }
    public ConfigurableParticleOptions setGravity(float gravity) {
        this.gravity = gravity;
        return this;
    }
    public static <T extends ConfigurableParticleOptions> MapCodec<T> createCodec(Supplier<T> createInstance) {
        MapCodec<T> codec = RecordCodecBuilder.mapCodec((builder) -> {
            return builder.group(
                    ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("color").forGetter((particle) -> particle.color.getRGB()),
                    Codec.BOOL.fieldOf("additive").forGetter((particle) -> particle.additive),
                    Codec.INT.fieldOf("lifetime").forGetter((particle) -> particle.lifetime),
                    Codec.FLOAT.fieldOf("friction").forGetter((particle) -> particle.friction),
                    Codec.FLOAT.fieldOf("gravity").forGetter((particle) -> particle.gravity)
            ).apply(builder, (color, additive, lifetime, friction, gravity) -> (T)createInstance.get().setColor(new Color(color)).setAdditive(additive).setLifetime(lifetime).setFriction(friction).setGravity(gravity));
        });
        return codec;
    }

    public static <T extends ConfigurableParticleOptions> StreamCodec<RegistryFriendlyByteBuf, T> createStreamCodec(Supplier<T> createInstance) {
        StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of((buf, options) -> {
            buf.writeInt(options.color.getRGB());
            buf.writeBoolean(options.additive);
            buf.writeInt(options.lifetime);
            buf.writeFloat(options.friction);
            buf.writeFloat(options.gravity);
        }, (buf) -> {
            int rgb = buf.readInt();
            boolean additive = buf.readBoolean();
            int lifetime = buf.readInt();
            float friction = buf.readFloat();
            float gravity = buf.readFloat();
            return (T)createInstance.get().setColor(new Color(rgb)).setAdditive(additive).setLifetime(lifetime).setFriction(friction).setGravity(gravity);
        });
        return codec;
    }
}
