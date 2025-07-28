package com.cmdpro.datanessence.datagen.recipe;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.registry.BlockRegistry;
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
        // Essence Shard Conversions
        new InfusionRecipeBuilder(new ItemStack(ItemRegistry.ESSENCE_SHARD.get()), Ingredient.of(Items.AMETHYST_SHARD), DataNEssence.locate("basics/essence_crystals"), -1, Map.of(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()), 100f)).unlockedBy("has_shard", has(Items.AMETHYST_SHARD)).save(output);
        // new InfusionRecipeBuilder(new ItemStack(ItemRegistry.LUNAR_ESSENCE_SHARD.get()), Ingredient.of(Items.AMETHYST_SHARD), DataNEssence.locate("basics/lunar_strikes"), -1, Map.of(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.LUNAR_ESSENCE.get()), 100f)).unlockedBy("has_shard", has(Items.AMETHYST_SHARD)).save(output);

        // Industrial Infuser Recipes
        new InfusionRecipeBuilder(new ItemStack(Items.NETHERITE_SCRAP, 2), Ingredient.of(Items.ANCIENT_DEBRIS), DataNEssence.locate("machinery/industrial_infuser_processing"), -1, Map.of(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()), 500f)).unlockedBy("has_debris", has(Items.ANCIENT_DEBRIS)).save(output);
        new InfusionRecipeBuilder(new ItemStack(Items.GLOWSTONE_DUST, 2), Ingredient.of(Items.GLOW_BERRIES), DataNEssence.locate("machinery/industrial_infuser_processing"), -1, Map.of(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()), 100f)).unlockedBy("has_berry", has(Items.GLOW_BERRIES)).save(output);
        new InfusionRecipeBuilder(new ItemStack(BlockRegistry.TRAVERSITE_ROAD.get().asItem()), Ingredient.of(Items.DIORITE), DataNEssence.locate("tools/traversite_road"), -1, Map.of(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()), 50f)).unlockedBy("has_diorite", has(Items.DIORITE)).save(output);
    }
}
