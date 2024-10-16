package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.api.misc.ICustomEssencePointBehaviour;
import com.cmdpro.datanessence.api.misc.ICustomFluidPointBehaviour;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.awt.*;

public class FluidPointBlockEntity extends BaseCapabilityPointBlockEntity {
    public FluidPointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FLUID_POINT.get(), pos, state);
    }
    private final FluidTank fluidHandler = new FluidTank(Integer.MAX_VALUE);
    public IFluidHandler getFluidHandler() {
        return lazyFluidHandler.get();
    }
    private Lazy<IFluidHandler> lazyFluidHandler = Lazy.of(() -> fluidHandler);
    @Override
    public Color linkColor() {
        return new Color(0x56bae9);
    }
    @Override
    public void transfer(BlockEntity other) {
        IFluidHandler resolved2 = level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos(), null);
        if (resolved2 == null) {
            return;
        }
        if (resolved2.getFluidInTank(0).getAmount() > 0) {
            return;
        }
        deposit(other);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("fluid", fluidHandler.writeToNBT(pRegistries, new CompoundTag()));
        super.saveAdditional(tag, pRegistries);
    }

    @Override
    public void deposit(BlockEntity other) {
        IFluidHandler resolved = level.getCapability(Capabilities.FluidHandler.BLOCK, other.getBlockPos(), getDirection());
        IFluidHandler resolved2 = level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos(), null);
        if (resolved == null || resolved2 == null) {
            return;
        }
        if (other instanceof ICustomFluidPointBehaviour behaviour) {
            if (!behaviour.canInsertFluid(resolved, resolved2)) {
                return;
            }
        }
        for (int o = 0; o < resolved2.getTanks(); o++) {
            FluidStack copy = resolved2.getFluidInTank(o).copy();
            if (!copy.isEmpty()) {
                copy.setAmount(Math.clamp(0, DataNEssenceConfig.fluidPointTransfer, copy.getAmount()));
                int filled = resolved.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                resolved2.getFluidInTank(o).shrink(filled);
            }
        }
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        fluidHandler.readFromNBT(pRegistries, nbt.getCompound("fluid"));
    }

    @Override
    public void take(BlockEntity other) {
        IFluidHandler resolved = level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos(), null);
        IFluidHandler resolved2 = level.getCapability(Capabilities.FluidHandler.BLOCK, other.getBlockPos(), getDirection());
        if (resolved == null || resolved2 == null) {
            return;
        }
        if (resolved.getFluidInTank(0).getAmount() > 0) {
            return;
        }
        if (other instanceof ICustomFluidPointBehaviour behaviour) {
            if (!behaviour.canExtractFluid(resolved2, resolved)) {
                return;
            }
        }
        for (int o = 0; o < resolved2.getTanks(); o++) {
            FluidStack copy = resolved2.getFluidInTank(o).copy();
            if (!copy.isEmpty()) {
                copy.setAmount(Math.clamp(0, DataNEssenceConfig.fluidPointTransfer, copy.getAmount()));
                int filled = resolved.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                resolved2.getFluidInTank(o).shrink(filled);
            }
        }
    }
}
