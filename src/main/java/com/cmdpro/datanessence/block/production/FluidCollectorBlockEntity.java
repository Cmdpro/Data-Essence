package com.cmdpro.datanessence.block.production;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    public int iterator; // what spot in the work area are we?
    public static boolean shouldRecalcWorkArea = true;
    public static BlockPos[] workArea;

    public FluidCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FLUID_COLLECTOR.get(), pos, state);
    }

    private final FluidTank fluidHandler = new FluidTank(4000);
    public IFluidHandler getFluidHandler() {
        return lazyFluidHandler.get();
    }
    private Lazy<IFluidHandler> lazyFluidHandler = Lazy.of(() -> fluidHandler);

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("EssenceStorage", storage.toNbt());
        tag.put("fluid", fluidHandler.writeToNBT(pRegistries, new CompoundTag()));
        tag.putInt("cooldown", cooldown);
        tag.putInt("SoundTick", soundTick);
        tag.putInt("Iterator", iterator);
        super.saveAdditional(tag, pRegistries);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        fluidHandler.readFromNBT(pRegistries, nbt.getCompound("fluid"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        cooldown = nbt.getInt("cooldown");
        soundTick = nbt.getInt("SoundTick");
        iterator = nbt.getInt("Iterator");
    }

    public static void tick(Level world, BlockPos tilePos, BlockState tileState, FluidCollectorBlockEntity tile) {

        if (!world.isClientSide()) {
            if (tile.cooldown <= 0) {
                if (tile.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()) >= 25) {
                    if (tile.getBlockState().getValue(FluidCollector.FACING) != Direction.DOWN) {
                        BlockPos pos = tilePos.relative(tileState.getValue(FluidCollector.FACING));
                        FluidState state = world.getFluidState(pos);
                        if (!state.isEmpty() && state.isSource()) {
                            FluidStack stack = new FluidStack(state.getType(), 1000);
                            if (tile.fluidHandler.fill(stack, IFluidHandler.FluidAction.SIMULATE) > 0) {
                                tile.fluidHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                world.playSound(null, tilePos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 2f, 0.75f);
                                tile.getStorage().removeEssence(EssenceTypeRegistry.ESSENCE.get(), 25);
                                tile.cooldown = 20;
                            }
                        }
                    }
                    else {;
                        if (shouldRecalcWorkArea) { // necessitate breaking and replacing to recheck positions
                            workArea = recalcArea(tilePos);
                            shouldRecalcWorkArea = false;
                        }

                        if (tile.iterator > workArea.length)
                            tile.iterator = 0;
                        BlockPos workPos = workArea[tile.iterator];

                        FluidState fluid = world.getFluidState(workPos);
                        if (!fluid.isEmpty() && fluid.isSource()) {
                            FluidStack fluidToBeCollected = new FluidStack(fluid.getType(), 1000);

                            if (tile.fluidHandler.fill(fluidToBeCollected, IFluidHandler.FluidAction.SIMULATE) > 0) {
                                tile.fluidHandler.fill(fluidToBeCollected, IFluidHandler.FluidAction.EXECUTE);
                                world.setBlockAndUpdate(workPos, Blocks.AIR.defaultBlockState());
                                world.playSound(null, workPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 2f, 0.75f);
                                tile.getStorage().removeEssence(EssenceTypeRegistry.ESSENCE.get(), 25);
                                tile.cooldown = 20;
                            }
                        }
                        tile.iterator++;
                    }
                }
            } else {
                tile.cooldown -= 1;
            }

            if (tile.soundTick <= 0) {
                world.playSound(null, tilePos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.25f, 1.5f);
                tile.soundTick = 17; // 18 desyncs eventually. 16 is too fast. 17 seems okay - on the low chance it matches up anyway :p
            }
            else { tile.soundTick -= 1; }
        }
    }

    public static BlockPos[] recalcArea(BlockPos tilePos) {
        BlockPos[] ret = new BlockPos[400]; // length should be total volume of area - 10*10*4
        int iterator = 0;
        for (BlockPos pos : BlockPos.betweenClosed(tilePos.offset(-5, -1, -5), tilePos.offset(5, -5, 5))) {
            ret[iterator] = pos;
            iterator++;
        }
        return ret;
    }
}
