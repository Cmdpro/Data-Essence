package com.cmdpro.datanessence.moddata;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;

public class MultiFluidTank implements IFluidHandler {
    private List<FluidTank> tanks;
    public MultiFluidTank(List<FluidTank> tanks) {
        this.tanks = tanks;
    }
    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return tanks.get(tank).getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return tanks.get(tank).getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tanks.get(tank).isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        FluidStack remaining = resource.copy();
        for (FluidTank i : tanks) {
            remaining.setAmount(remaining.getAmount()-i.fill(remaining, action));
            if (remaining.getAmount() <= 0) {
                break;
            }
        }
        return resource.getAmount()-remaining.getAmount();
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack remaining = resource.copy();
        for (FluidTank i : tanks) {
            remaining.setAmount(remaining.getAmount()-i.drain(remaining, action).getAmount());
            if (remaining.getAmount() <= 0) {
                break;
            }
        }
        remaining.setAmount(resource.getAmount()-remaining.getAmount());
        return remaining;
    }


    public MultiFluidTank readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        int o = 0;
        for (Tag i : (ListTag)nbt.get("tanks")) {
            tanks.get(o).readFromNBT(lookupProvider, (CompoundTag)i);
            o++;
        }
        return this;
    }

    public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        ListTag tag = new ListTag();
        for (FluidTank i : tanks) {
            tag.add(i.writeToNBT(lookupProvider, new CompoundTag()));
        }
        nbt.put("tanks", tag)
        return nbt;
    }
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int remaining = maxDrain;
        FluidStack drainStack = FluidStack.EMPTY;
        for (FluidTank i : tanks) {
            FluidStack drain = i.drain(maxDrain, action);
            remaining -= drain.getAmount();
            if (remaining <= 0) {
                break;
            }
        }
        remaining = maxDrain-remaining;
        drainStack.setAmount(remaining);
        return drainStack;
    }
}
