package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.recipe.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeInit {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, DataNEssence.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, DataNEssence.MOD_ID);
    public static final RegistryObject<RecipeSerializer<ShapelessFabricationRecipe>> SHAPELESSFABRICATIONRECIPE = RECIPES.register("shapelessfabricationrecipe", () -> ShapelessFabricationRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<ShapedFabricationRecipe>> SHAPEDFABRICATIONRECIPE = RECIPES.register("shapedfabricationrecipe", () -> ShapedFabricationRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<IFabricationRecipe>> FABRICATIONCRAFTING =
            RECIPE_TYPES.register("fabricationrecipe", () -> RecipeType.simple(new ResourceLocation(DataNEssence.MOD_ID, "runicrecipe")));
    public static void register(IEventBus eventBus) {
        RECIPES.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
