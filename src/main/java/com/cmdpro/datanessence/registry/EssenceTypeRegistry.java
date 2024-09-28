package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.essence.EssenceType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EssenceTypeRegistry {
    public static final DeferredRegister<EssenceType> ESSENCE_TYPES = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "essence_types"), DataNEssence.MOD_ID);
    public static final Supplier<EssenceType> ESSENCE = register("essence", () -> new EssenceType(Component.translatable("datanessence.essence_types.essence"), 0xe236ef, "gui.essence_bar.essence", EssenceType.createLocation(EssenceType.ESSENCE_BAR_LOCATION, 0, 56), EssenceType.createLocation(EssenceType.ESSENCE_BAR_LOCATION, 0, 0))); // Artifice, industry, electricity
    public static final Supplier<EssenceType> LUNAR_ESSENCE = register("lunar_essence", () -> new EssenceType(Component.translatable("datanessence.essence_types.lunar_essence"), 0xf5fbc0, "gui.essence_bar.lunar_essence", EssenceType.createLocation(EssenceType.ESSENCE_BAR_LOCATION, 11, 56), EssenceType.createLocation(EssenceType.ESSENCE_BAR_LOCATION, 11, 0))); // Transformation, the moon, rule-bending
    public static final Supplier<EssenceType> NATURAL_ESSENCE = register("natural_essence", () -> new EssenceType(Component.translatable("datanessence.essence_types.natural_essence"), 0x57f36c, "gui.essence_bar.natural_essence", EssenceType.createLocation(EssenceType.ESSENCE_BAR_LOCATION, 22, 56), EssenceType.createLocation(EssenceType.ESSENCE_BAR_LOCATION, 22, 0))); // Nature, life, the world
    public static final Supplier<EssenceType> EXOTIC_ESSENCE = register("exotic_essence", () -> new EssenceType(Component.translatable("datanessence.essence_types.exotic_essence"), 0xe7fcf9, "gui.essence_bar.exotic_essence", EssenceType.createLocation(EssenceType.ESSENCE_BAR_LOCATION, 33, 56), EssenceType.createLocation(EssenceType.ESSENCE_BAR_LOCATION, 33, 0))); // Stars, space, dreams
    // How *strange*. It feels like something's missing.
    private static <T extends EssenceType> Supplier<T> register(final String name, final Supplier<T> item) {
        return ESSENCE_TYPES.register(name, item);
    }
}
