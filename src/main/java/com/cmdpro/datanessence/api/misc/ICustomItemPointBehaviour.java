package com.cmdpro.datanessence.api.misc;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public interface ICustomItemPointBehaviour {
    default boolean canExtractItem(IItemHandler handler, IItemHandler other) {
        return true;
    }
    default boolean canInsertItem(IItemHandler handler, IItemHandler other) {
        return true;
    }
}
