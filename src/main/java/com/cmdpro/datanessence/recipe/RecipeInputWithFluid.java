package com.cmdpro.datanessence.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class RecipeInputWithFluid implements RecipeInput {
    public RecipeInput input;
    private List<FluidStack> fluids;
    public RecipeInputWithFluid(RecipeInput input, List<FluidStack> fluids) {
        this.input = input;
        this.fluids = fluids;
    }
    @Override
    public ItemStack getItem(int pIndex) {
        return input.getItem(pIndex);
    }

    @Override
    public int size() {
        return input.size();
    }
    @Override
    public boolean isEmpty() {
        return input.isEmpty();
    }
    public int fluidSize() {
        return fluids.size();
    }
    public FluidStack getFluid(int pIndex) {
        return fluids.get(pIndex);
    }
}
