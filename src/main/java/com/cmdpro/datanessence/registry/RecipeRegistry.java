package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.recipe.*;
import net.minecraft.core.registries.BuiltInRegistries;
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
    public static final Supplier<RecipeSerializer<SynthesisRecipe>> SYNTHESIS =
            registerSerializer("synthesis", () -> SynthesisRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<FluidMixingRecipe>> FLUID_MIXING =
            registerSerializer("fluid_mixing", () -> FluidMixingRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<MineralPurificationRecipe>> MINERAL_PURIFICATION =
            registerSerializer("mineral_purification", () -> MineralPurificationRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<MetalShaperRecipe>> METAL_SHAPING =
            registerSerializer("metal_shaping", () -> MetalShaperRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<MeltingRecipe>> MELTING =
            registerSerializer("melting", () -> MeltingRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<DryingRecipe>> DRYING =
            registerSerializer("drying", () -> DryingRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeSerializer<GenderfluidTransitionRecipe>> GENDERFLUID_TRANSITION =
            registerSerializer("genderfluid_transition", () -> GenderfluidTransitionRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeType<FluidMixingRecipe>> FLUID_MIXING_TYPE =
            registerBasicRecipeType("fluid_mixing");
    public static final Supplier<RecipeType<SynthesisRecipe>> SYNTHESIS_TYPE =
            registerBasicRecipeType("synthesis");
    public static final Supplier<RecipeType<MetalShaperRecipe>> METAL_SHAPING_TYPE =
            registerBasicRecipeType("metal_shaping");
    public static final Supplier<RecipeType<EntropicProcessingRecipe>> ENTROPIC_PROCESSING_TYPE =
            registerBasicRecipeType("entropic_processing");
    public static final Supplier<RecipeType<InfusionRecipe>> INFUSION_TYPE =
            registerBasicRecipeType("infusion");
    public static final Supplier<RecipeType<MineralPurificationRecipe>> MINERAL_PURIFICATION_TYPE =
            registerBasicRecipeType("mineral_purification");
    public static final Supplier<RecipeType<IFabricationRecipe>> FABRICATIONCRAFTING =
            registerBasicRecipeType("fabrication_recipe");
    public static final Supplier<RecipeType<MeltingRecipe>> MELTING_TYPE =
            registerBasicRecipeType("melting");
    public static final Supplier<RecipeType<DryingRecipe>> DRYING_TYPE =
            registerBasicRecipeType("drying");
    public static final Supplier<RecipeType<GenderfluidTransitionRecipe>> GENDERFLUID_TRANSITION_TYPE =
            registerBasicRecipeType("genderfluid_transition");

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
