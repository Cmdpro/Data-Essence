package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.recipe.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, DataNEssence.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, DataNEssence.MOD_ID);
    public static final Supplier<RecipeSerializer<ShapelessFabricationRecipe>> SHAPELESSFABRICATIONRECIPE = registerSerializer("shapeless_fabrication_recipe", () -> ShapelessFabricationRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<ShapedFabricationRecipe>> SHAPEDFABRICATIONRECIPE = registerSerializer("shaped_fabrication_recipe", () -> ShapedFabricationRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<InfusionRecipe>> INFUSION =
            registerSerializer("infusion", () -> InfusionRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<EntropicProcessingRecipe>> ENTROPIC_PROCESSING =
            registerSerializer("entropic_processing", () -> EntropicProcessingRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeType<EntropicProcessingRecipe>> ENTROPIC_PROCESSING_TYPE =
            registerBasicRecipeType("entropic_processing");
    public static final Supplier<RecipeType<InfusionRecipe>> INFUSION_TYPE =
            registerBasicRecipeType("infusion");
    public static final Supplier<RecipeType<IFabricationRecipe>> FABRICATIONCRAFTING =
            registerBasicRecipeType("fabrication_recipe");

    private static <T extends RecipeType<?>> Supplier<T> registerType(final String name, final Supplier<T> recipe) {
        return RECIPE_TYPES.register(name, recipe);
    }
    static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> registerBasicRecipeType(final String id) {
        return RECIPE_TYPES.register(id, () -> new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }
    private static <T extends RecipeSerializer<?>> Supplier<T> registerSerializer(final String name, final Supplier<T> recipe) {
        return RECIPES.register(name, recipe);
    }
}
