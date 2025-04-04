package com.cmdpro.datanessence.api.node;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface ICustomFluidPointBehaviour {
    default boolean canExtractFluid(IFluidHandler from, IFluidHandler to) {
        return true;
    }
    default boolean canInsertFluid(IFluidHandler from, IFluidHandler to) {
        return true;
    }
}