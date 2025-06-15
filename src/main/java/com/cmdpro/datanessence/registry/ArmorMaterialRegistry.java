package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.worldgen.features.EssenceCrystalFeature;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ArmorMaterialRegistry {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL,
            DataNEssence.MOD_ID);
    public static final Holder<ArmorMaterial> PRIMITIVE_ANTI_GRAVITY_PACK = register("primitive_anti_gravity_pack", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.CHESTPLATE, 2);
            }),
            15,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            () -> Ingredient.of(Tags.Items.INGOTS_COPPER), List.of(
                    new ArmorMaterial.Layer(
                            DataNEssence.locate("primitive_anti_gravity_pack")
                    )
            ),
            0,
            0
    ));

    public static final Holder<ArmorMaterial> TRAVERSITE_TRUDGERS = register("traversite_trudgers", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 1);
            }),
            15,
            SoundRegistry.EQUIP_TRAVERSITE_TRUDGERS,
            () -> Ingredient.of(Tags.Items.LEATHERS), List.of(
            new ArmorMaterial.Layer(
                    DataNEssence.locate("traversite_trudgers")
            )
    ),
            0,
            0
    ));

    private static <T extends ArmorMaterial> DeferredHolder<ArmorMaterial, T> register(final String name, final Supplier<T> material) {
        return ARMOR_MATERIALS.register(name, material);
    }
}
