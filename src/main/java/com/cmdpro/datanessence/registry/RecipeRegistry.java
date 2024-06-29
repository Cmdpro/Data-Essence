package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.recipe.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, DataNEssence.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, DataNEssence.MOD_ID);
    public static final RegistryObject<RecipeSerializer<ShapelessFabricationRecipe>> SHAPELESSFABRICATIONRECIPE = registerSerializer("shapeless_fabrication_recipe", () -> ShapelessFabricationRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<ShapedFabricationRecipe>> SHAPEDFABRICATIONRECIPE = registerSerializer("shaped_fabrication_recipe", () -> ShapedFabricationRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<InfusionRecipe>> INFUSION =
            registerSerializer("infusion", () -> InfusionRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<IFabricationRecipe>> FABRICATIONCRAFTING =
            registerType("fabrication_recipe", () -> RecipeType.simple(new ResourceLocation(DataNEssence.MOD_ID, "fabrication_recipe")));

    private static <T extends RecipeType<?>> RegistryObject<T> registerType(final String name, final Supplier<T> recipe) {
        return RECIPE_TYPES.register(name, recipe);
    }
    private static <T extends RecipeSerializer<?>> RegistryObject<T> registerSerializer(final String name, final Supplier<T> recipe) {
        return RECIPES.register(name, recipe);
    }
}
