package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.worldgen.features.EssenceCrystalFeature;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class FeatureInit {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES,
            DataNEssence.MOD_ID);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ESSENCE_CRYSTAL = register("essence_crystal", () -> new EssenceCrystalFeature(NoneFeatureConfiguration.CODEC));

    private static <T extends Feature<?>> RegistryObject<T> register(final String name, final Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }
}
