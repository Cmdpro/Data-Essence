package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.MultiFluidTankNoDuplicateFluids;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.List;

public class FluidBufferBlockEntity extends BlockEntity {
    private final MultiFluidTankNoDuplicateFluids fluidHandler = new MultiFluidTankNoDuplicateFluids(List.of(new FluidTank(1000), new FluidTank(1000), new FluidTank(1000), new FluidTank(1000), new FluidTank(1000), new FluidTank(1000)));
    public FluidBufferBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.FLUID_BUFFER.get(), pPos, pBlockState);
    }
    public FluidBufferBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    public void transfer(IFluidHandler handler) {
        IFluidHandler resolved = getFluidHandler();
        for (int o = 0; o < resolved.getTanks(); o++) {
            FluidStack copy = resolved.getFluidInTank(o).copy();
            if (!copy.isEmpty()) {
                copy.setAmount(Math.clamp(0, 4000, copy.getAmount()));
                int filled = handler.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                resolved.getFluidInTank(o).shrink(filled);
            }
        }
    }
    public IFluidHandler getFluidHandler() {
        return lazyFluidHandler.get();
    }
    private Lazy<IFluidHandler> lazyFluidHandler = Lazy.of(() -> fluidHandler);
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("fluid", fluidHandler.writeToNBT(pRegistries, new CompoundTag()));
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        fluidHandler.readFromNBT(pRegistries, nbt.getCompound("fluid"));
    }
}
