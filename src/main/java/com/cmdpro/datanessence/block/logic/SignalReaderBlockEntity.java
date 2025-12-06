package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.api.signal.IStructuralSignalReceiver;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignalReaderBlockEntity extends BlockEntity implements IStructuralSignalReceiver {

    private String lastSignalId = "";
    private int lastSignalAmount = 0;

    public SignalReaderBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SIGNAL_READER.get(), pos, state);
    }

    //Receiver API

    @Override
    public void acceptSignal(String signalId, int amount) {
        this.lastSignalId = signalId;
        this.lastSignalAmount = amount;
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    public String getLastSignalId() {
        return lastSignalId;
    }

    public int getLastSignalAmount() {
        return lastSignalAmount;
    }

    //NBT

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("LastSignalId", lastSignalId);
        tag.putInt("LastSignalAmount", lastSignalAmount);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("LastSignalId")) {
            lastSignalId = tag.getString("LastSignalId");
        }
        if (tag.contains("LastSignalAmount")) {
            lastSignalAmount = tag.getInt("LastSignalAmount");
        }
    }
}
