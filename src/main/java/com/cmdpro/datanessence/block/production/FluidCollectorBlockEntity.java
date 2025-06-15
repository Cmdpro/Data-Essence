package com.cmdpro.datanessence.block.production;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class FluidCollectorBlockEntity extends BlockEntity implements EssenceBlockEntity {
    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 1000);
    @Override
    public EssenceStorage getStorage() {
        return storage;
    }
    public int cooldown;
    public int soundTick; // for texture animation-synced noise
    public FluidCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FLUID_COLLECTOR.get(), pos, state);
    }
    private final FluidTank fluidHandler = new FluidTank(4000);
    public IFluidHandler getFluidHandler() {
        return fluidHandler;
    }
    
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("EssenceStorage", storage.toNbt());
        tag.put("fluid", fluidHandler.writeToNBT(pRegistries, new CompoundTag()));
        tag.putInt("cooldown", cooldown);
        tag.putInt("SoundTick", soundTick);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        fluidHandler.readFromNBT(pRegistries, nbt.getCompound("fluid"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        cooldown = nbt.getInt("cooldown");
        soundTick = nbt.getInt("SoundTick");
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FluidCollectorBlockEntity pBlockEntity) {

        if (!pLevel.isClientSide()) {
            if (pBlockEntity.cooldown <= 0) {
                if (pBlockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()) >= 25) {
                    BlockPos pos = pPos.relative(pState.getValue(FluidCollector.FACING));
                    FluidState state = pLevel.getFluidState(pos);
                    if (!state.isEmpty() && state.isSource()) {
                        FluidStack stack = new FluidStack(state.getType(), 1000);
                        if (pBlockEntity.fluidHandler.fill(stack, IFluidHandler.FluidAction.SIMULATE) > 0) {
                            pBlockEntity.fluidHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                            pLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                            pLevel.playSound(null, pPos, SoundRegistry.FLUID_COLLECTOR_FILL.value(), SoundSource.BLOCKS, 2f, 1f);
                            pBlockEntity.getStorage().removeEssence(EssenceTypeRegistry.ESSENCE.get(), 25);
                            pBlockEntity.cooldown = 20;
                        }
                    }
                }
            } else {
                pBlockEntity.cooldown -= 1;
            }

            if (pBlockEntity.soundTick <= 0) {
                pLevel.playSound(null, pPos, SoundRegistry.FLUID_COLLECTOR_TICK.value(), SoundSource.BLOCKS, 0.25f, 1f);
                pBlockEntity.soundTick = 17; // 18 desyncs eventually. 16 is too fast. 17 seems okay - on the low chance it matches up anyway :p
            }
            else { pBlockEntity.soundTick -= 1; }
        }
    }
}
