package com.cmdpro.datanessence.datagen.recipe;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DnEREcipeProvider extends RecipeProvider {

    public DnEREcipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        new InfusionRecipeBuilder(new ItemStack(ItemRegistry.LUNAR_ESSENCE_SHARD.get()), Ingredient.of(Items.AMETHYST_SHARD), DataNEssence.locate("basics/lunar_strikes"), -1, Map.of(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.LUNAR_ESSENCE.get()), 100f)).unlockedBy("has_shard", has(ItemRegistry.LUNAR_ESSENCE_SHARD.get())).save(output);
    }
}
