package com.cmdpro.datanessence.block.storage;

import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
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
