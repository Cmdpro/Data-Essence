package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.worldgen.features.EssenceCrystalFeature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FeatureRegistry {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE,
            DataNEssence.MOD_ID);
    public static final Supplier<Feature<NoneFeatureConfiguration>> ESSENCE_CRYSTAL = register("essence_crystal", () -> new EssenceCrystalFeature(NoneFeatureConfiguration.CODEC));

    private static <T extends Feature<?>> Supplier<T> register(final String name, final Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }
}
