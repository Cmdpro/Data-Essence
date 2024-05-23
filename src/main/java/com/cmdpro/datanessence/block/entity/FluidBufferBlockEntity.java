package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidBufferBlockEntity extends BlockEntity {
    private final FluidTank fluidHandler = new FluidTank(1000);
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public FluidBufferBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.FLUIDBUFFER.get(), pPos, pBlockState);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyFluidHandler = LazyOptional.of(() -> fluidHandler);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("fluid", fluidHandler.writeToNBT(new CompoundTag()));
        super.saveAdditional(tag);
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        fluidHandler.readFromNBT(nbt.getCompound("fluid"));
    }
    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
