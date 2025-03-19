package com.cmdpro.datanessence.api.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Because this is for some reason not a component - I would've thought it would be?
 */
public class FuelItem extends Item {
    public int burnTime;

    public FuelItem(Properties properties, int burnTime) {
        super(properties);
        this.burnTime = burnTime;
    }

    @Override
    public int getBurnTime(ItemStack stack, RecipeType<?> type) {
        return burnTime;
    }
}
