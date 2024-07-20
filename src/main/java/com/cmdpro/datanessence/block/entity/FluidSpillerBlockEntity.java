package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.block.FluidCollector;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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

public class FluidSpillerBlockEntity extends EssenceContainer {
    @Override
    public float getMaxEssence() {
        return 1000;
    }
    private final FluidTank fluidHandler = new FluidTank(4000);
    public FluidSpillerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FLUID_SPILLER.get(), pos, state);
    }
    public IFluidHandler getFluidHandler() {
        return lazyFluidHandler.get();
    }
    private Lazy<IFluidHandler> lazyFluidHandler = Lazy.of(() -> fluidHandler);
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.putFloat("essence", getEssence());
        tag.put("fluid", fluidHandler.writeToNBT(pRegistries, new CompoundTag()));
        tag.putInt("cooldown", cooldown);
        super.saveAdditional(tag, pRegistries);
    }
    public int cooldown;
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        fluidHandler.readFromNBT(pRegistries, nbt.getCompound("fluid"));
        setEssence(nbt.getFloat("essence"));
        cooldown = nbt.getInt("cooldown");
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FluidSpillerBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            if (pBlockEntity.cooldown <= 0) {
                if (pBlockEntity.getEssence() >= 25) {
                    BlockPos pos = pPos.relative(pState.getValue(FluidCollector.FACING));
                    FluidState state = pLevel.getFluidState(pos);
                    BlockState state2 = pLevel.getBlockState(pos);
                    FluidStack stack = new FluidStack(pBlockEntity.fluidHandler.getFluid().getFluid(), 1000);
                    if ((state.isEmpty() || !state.isSource()) && state2.canBeReplaced(stack.getFluid())) {
                        if (pBlockEntity.fluidHandler.getCapacity() >= 1000) {
                            pBlockEntity.fluidHandler.drain(stack, IFluidHandler.FluidAction.EXECUTE);
                            pLevel.setBlockAndUpdate(pos, stack.getFluid().defaultFluidState().createLegacyBlock());
                            pBlockEntity.setEssence(pBlockEntity.getEssence() - 25);
                        }
                    }
                }
            } else {
                pBlockEntity.cooldown -= 1;
            }
        }
    }
}
