package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.particle.CircleParticleOptions;
import com.cmdpro.datanessence.particle.CircleParticleOptionsAdditive;
import com.cmdpro.datanessence.worldgen.features.EssenceCrystalFeature;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE,
            DataNEssence.MOD_ID);
    public static final Supplier<SimpleParticleType> ESSENCE_SPARKLE =
            register("essence_sparkle", () -> new SimpleParticleType(false));
    public static final Supplier<ParticleType> CIRCLE =
            register("circle", () -> new ParticleType<CircleParticleOptions>(false) {

                @Override
                public MapCodec<CircleParticleOptions> codec() {
                    return CircleParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, CircleParticleOptions> streamCodec() {
                    return CircleParticleOptions.STREAM_CODEC;
                }
            });
    public static final Supplier<ParticleType> CIRCLE_ADDITIVE =
            register("circle_additive", () -> new ParticleType<CircleParticleOptionsAdditive>(false) {

                @Override
                public MapCodec<CircleParticleOptionsAdditive> codec() {
                    return CircleParticleOptionsAdditive.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, CircleParticleOptionsAdditive> streamCodec() {
                    return CircleParticleOptionsAdditive.STREAM_CODEC;
                }
            });

    private static <T extends ParticleType<?>> Supplier<T> register(final String name, final Supplier<T> particle) {
        return PARTICLE_TYPES.register(name, particle);
    }
}
