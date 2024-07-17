package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.block.FluidCollector;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class FluidCollectorBlockEntity extends BlockEntity {
    private final FluidTank fluidHandler = new FluidTank(4000);
    public FluidCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FLUID_COLLECTOR.get(), pos, state);
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
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FluidCollectorBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BlockPos pos = pPos.relative(pState.getValue(FluidCollector.FACING));
            FluidState state = pLevel.getFluidState(pos);
            if (!state.isEmpty() && state.isSource()) {
                FluidStack stack = new FluidStack(state.getType(), 1000);
                if (pBlockEntity.fluidHandler.fill(stack, IFluidHandler.FluidAction.SIMULATE) > 0) {
                    pBlockEntity.fluidHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                    pLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }
}
