package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.api.signal.IStructuralSignalProvider;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignalEmitterBlockEntity extends BlockEntity
        implements IStructuralSignalProvider, MenuProvider {

    /**
     * Generic signal id.
     * - char mode: "A", "5", etc.
     * - block mode: "block:minecraft:stone"
     */
    private String signalId = "X";
    private int amount = 1;

    public SignalEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SIGNAL_EMITTER.get(), pos, state);
    }

    // IStructuralSignalProvider

    @Override
    public String getSignalId() {
        return signalId;
    }

    @Override
    public int getSignalAmount() {
        return amount;
    }

    // setters used by menu

    public void setCharSignal(char c) {
        this.signalId = String.valueOf(c);
        setDirtyAndSync();
    }

    public void setBlockSignal(ResourceLocation blockId) {
        this.signalId = "block:" + blockId.toString();
        setDirtyAndSync();
    }

    public void setSignalId(String id) {
        this.signalId = id;
        setDirtyAndSync();
    }

    public void setAmount(int amount) {
        this.amount = amount;
        setDirtyAndSync();
    }

    public boolean isBlockSignal() {
        return signalId != null && signalId.startsWith("block:");
    }

    public ResourceLocation getBlockSignalId() {
        if (!isBlockSignal()) return null;
        String raw = signalId.substring("block:".length());
        try {
            return ResourceLocation.parse(raw);
        } catch (Exception e) {
            return null;
        }
    }

    private void setDirtyAndSync() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    // MenuProvider

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.datanessence.signal_emitter");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new SignalEmitterMenu(id, inv, this);
    }

    //NBT

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
