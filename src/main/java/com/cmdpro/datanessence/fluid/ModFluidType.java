package com.cmdpro.datanessence.fluid;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;

public abstract class ModFluidType {
    public abstract FluidType createFluidType(ModFluidRegistryObject obj);
    public abstract FlowingFluid createSourceFluid(ModFluidRegistryObject obj);
    public abstract FlowingFluid createFlowingFluid(ModFluidRegistryObject obj);
    public abstract LiquidBlock createBlock(ModFluidRegistryObject obj);
    public Item createBucket(ModFluidRegistryObject obj) {
        return null;
    }
    public boolean hasBucket() {
        return false;
    }
}
