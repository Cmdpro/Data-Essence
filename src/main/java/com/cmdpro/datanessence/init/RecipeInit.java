package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.recipe.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RecipeInit {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, DataNEssence.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, DataNEssence.MOD_ID);
    public static final RegistryObject<RecipeSerializer<ShapelessFabricationRecipe>> SHAPELESSFABRICATIONRECIPE = registerSerializer("shapelessfabricationrecipe", () -> ShapelessFabricationRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<ShapedFabricationRecipe>> SHAPEDFABRICATIONRECIPE = registerSerializer("shapedfabricationrecipe", () -> ShapedFabricationRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<IFabricationRecipe>> FABRICATIONCRAFTING =
            registerType("fabricationrecipe", () -> RecipeType.simple(new ResourceLocation(DataNEssence.MOD_ID, "fabricationrecipe")));

    private static <T extends RecipeType<?>> RegistryObject<T> registerType(final String name, final Supplier<T> recipe) {
        return RECIPE_TYPES.register(name, recipe);
    }
    private static <T extends RecipeSerializer<?>> RegistryObject<T> registerSerializer(final String name, final Supplier<T> recipe) {
        return RECIPES.register(name, recipe);
    }
}
