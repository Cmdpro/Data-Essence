package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.worldgen.features.EssenceCrystalFeature;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES,
            DataNEssence.MOD_ID);
    public static final RegistryObject<SimpleParticleType> ESSENCE_SPARKLE =
            register("essence_sparkle", () -> new SimpleParticleType(true));

    private static <T extends ParticleType<?>> RegistryObject<T> register(final String name, final Supplier<T> particle) {
        return PARTICLE_TYPES.register(name, particle);
    }
}
