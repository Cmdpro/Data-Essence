package com.cmdpro.datanessence.api.misc;

import com.cmdpro.datanessence.api.essence.EssenceType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public interface ICustomFluidPointBehaviour {
    default boolean canExtractFluid(IFluidHandler handler, IFluidHandler other) {
        return true;
    }
    default boolean canInsertFluid(IFluidHandler handler, IFluidHandler other) {
        return true;
    }
}