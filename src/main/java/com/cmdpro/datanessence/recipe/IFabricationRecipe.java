package com.cmdpro.datanessence.recipe;

import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IFabricationRecipe extends CraftingRecipe, IHasRequiredKnowledge, IHasEssenceCost {
    @Override
    default RecipeType<?> getType() {
        return RecipeRegistry.FABRICATIONCRAFTING.get();
    }

    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    default ItemStack getToastSymbol() {
        return new ItemStack(BlockRegistry.FABRICATOR.get());
    }

    @Override
    default CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }
}
