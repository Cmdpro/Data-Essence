package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = DataNEssence.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class PotionRegistry {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION,
            DataNEssence.MOD_ID);
    public static final Holder<Potion> GENDER_EUPHORIA = register("gender_euphoria", () -> new Potion(new MobEffectInstance(MobEffectRegistry.GENDER_EUPHORIA, 30*20)));
    public static final Holder<Potion> LONG_GENDER_EUPHORIA = register("long_gender_euphoria", () -> new Potion(new MobEffectInstance(MobEffectRegistry.GENDER_EUPHORIA, 60*20)));

    private static <T extends Potion> DeferredHolder<Potion, T> register(final String name, final Supplier<T> potion) {
        return POTIONS.register(name, potion);
    }
    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addMix(Potions.REGENERATION, ItemRegistry.TRAVERSITE_ROAD_CHUNK.get(), GENDER_EUPHORIA);
        event.getBuilder().addMix(Potions.LONG_REGENERATION, ItemRegistry.TRAVERSITE_ROAD_CHUNK.get(), LONG_GENDER_EUPHORIA);
        event.getBuilder().addMix(GENDER_EUPHORIA, Items.REDSTONE, LONG_GENDER_EUPHORIA);
    }
}
