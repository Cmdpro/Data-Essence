package com.cmdpro.datanessence.api.block;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface Overheatable {

    /**
     * An overheatable block can sustain temperatures only up to this amount, after which it will fail.
     */
    int getMaxTemperature();

    /**
     * Upon going over temperature, an overheatable block will fail. This defines exactly how.
     * @param block What to perform the failure on
     */
    void overheat(BlockEntity block);
}
