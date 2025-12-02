package com.cmdpro.datanessence.block.storage;

import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class FluidTankBlockEntity extends BlockEntity {
    private final FluidTank fluidHandler = new FluidTank(16000) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            updateBlock();
        }
    };
    public static void serverTick(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity tank) {
        if (level.isClientSide) {
            return;
        }

        BlockPos belowPos = pos.below();
        BlockEntity belowBE = level.getBlockEntity(belowPos);
        if (!(belowBE instanceof FluidTankBlockEntity belowTank)) {
            return;
        }

        IFluidHandler source = tank.getFluidHandler();
        IFluidHandler dest = belowTank.getFluidHandler();

        FluidStack contained = source.getFluidInTank(0);
        if (contained.isEmpty()) {
            return;
        }
        int maxTransfer = 1000; // this changes how many mB travels each tick // maybe change this for balance later
        int amountToMove = Math.min(contained.getAmount(), maxTransfer);
        if (amountToMove <= 0) {
            return;
        }

        FluidStack toMove = contained.copy();
        toMove.setAmount(amountToMove);

        int filled = dest.fill(toMove, IFluidHandler.FluidAction.EXECUTE);
        if (filled > 0) {
            source.drain(filled, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        fluidHandler.readFromNBT(pRegistries, tag.getCompound("fluid"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("fluid", fluidHandler.writeToNBT(pRegistries, new CompoundTag()));
        return tag;
    }

    public FluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FLUID_TANK.get(), pos, state);
    }

    public IFluidHandler getFluidHandler() {
        return fluidHandler;
    }

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
