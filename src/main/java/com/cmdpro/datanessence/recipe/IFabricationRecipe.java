package com.cmdpro.datanessence.recipe;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

public interface IFabricationRecipe extends Recipe<CraftingInput>, IHasRequiredKnowledge, IHasEssenceCost, DataNEssenceRecipe {
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
    int getTime();

    @Override
    default ResourceLocation getMachineEntry() {
        return DataNEssence.locate("basics/fabricator");
    }
}
