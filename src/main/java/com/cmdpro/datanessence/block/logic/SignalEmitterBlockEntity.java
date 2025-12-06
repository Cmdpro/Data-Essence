package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.api.signal.IStructuralSignalProvider;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignalEmitterBlockEntity extends BlockEntity implements IStructuralSignalProvider {

    private String signalId = "hellow wowd!";
    private int amount = 69;

    public SignalEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SIGNAL_EMITTER.get(), pos, state);
    }


    @Override
    public String getSignalId() {
        return signalId;
    }

    @Override
    public int getSignalAmount() {
        return amount;
    }

    //Future UI hooks

    public void setSignalId(String id) {
        this.signalId = id;
        setChanged();
    }

    public void setAmount(int amount) {
        this.amount = amount;
        setChanged();
    }

    // nbt

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("SignalId", signalId);
        tag.putInt("SignalAmount", amount);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("SignalId")) {
            signalId = tag.getString("SignalId");
        }
        if (tag.contains("SignalAmount")) {
            amount = tag.getInt("SignalAmount");
        }
    }
}
