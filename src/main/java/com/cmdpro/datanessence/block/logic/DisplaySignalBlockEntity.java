package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.api.signal.IStructuralSignalReceiver;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class DisplaySignalBlockEntity extends BlockEntity
        implements IStructuralSignalReceiver, MenuProvider {

    private final Map<String, Integer> signals = new HashMap<>();

    private int selectedIndex = 0;
    private long lastUpdateTick = -1L;

    public DisplaySignalBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.DISPLAY_SIGNAL.get(), pos, state);
    }

    @Override
    public void acceptSignals(Map<String, Integer> signalsIn) {
        Level lvl = this.level;
        if (lvl == null || lvl.isClientSide) return;

        long tick = lvl.getGameTime();

        if (tick != lastUpdateTick) {
            signals.clear();
            lastUpdateTick = tick;
        }

        for (Map.Entry<String, Integer> e : signalsIn.entrySet()) {
            String id = e.getKey();
            int amount = e.getValue();
            if (amount <= 0) continue;
            signals.merge(id, amount, Integer::sum);
        }

        setChanged();
        BlockState st = lvl.getBlockState(worldPosition);
        lvl.sendBlockUpdated(worldPosition, st, st, 3);
    }


    public ItemStack getDisplayedStack() {
        String id = getDisplayId();
        if (id == null || id.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int amount = getDisplayAmount();
        if (amount <= 0) {
            amount = 1;
        }

        String raw = id;
        boolean explicitBlock = false;
        boolean explicitItem = false;

        // Optional prefixes to force block / item
        if (raw.startsWith("block:")) {
            explicitBlock = true;
            raw = raw.substring("block:".length());
        } else if (raw.startsWith("item:")) {
            explicitItem = true;
            raw = raw.substring("item:".length());
        }

        ResourceLocation rl = ResourceLocation.tryParse(raw);
        if (rl == null) {
            return ItemStack.EMPTY;
        }

        // If not explicitly item, try block first
        if (!explicitItem) {
            Block block = BuiltInRegistries.BLOCK.get(rl);
            if (block != Blocks.AIR) {
                return new ItemStack(block, 1);
            }
        }

        // Then try item
        if (!explicitBlock) {
            Item item = BuiltInRegistries.ITEM.get(rl);
            if (item != Items.AIR) {
                return new ItemStack(item, amount);
            }
        }

        return ItemStack.EMPTY;
    }

    private void clampSelectedIndexToSignals() {
        int count = getSignalCount();
        if (count <= 0) {
            // Keep whatever index the player chose; just nothing to display now
            return;
        }
        if (selectedIndex < 0) {
            selectedIndex = 0;
        } else if (selectedIndex >= count) {
            selectedIndex = count - 1;
        }
    }

    private List<String> getOrderedIds() {
        List<String> list = new ArrayList<>(signals.keySet());
        list.sort(String::compareTo);
        return list;
    }

    public int getSignalCount() {
        return signals.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
        clampSelectedIndexToSignals();
        Level lvl = this.level;
        if (lvl != null && !lvl.isClientSide) {
            setChanged();
            BlockState st = lvl.getBlockState(worldPosition);
            lvl.sendBlockUpdated(worldPosition, st, st, 3);
        }
    }

    public String getDisplayId() {
        if (signals.isEmpty()) return "";
        List<String> ids = getOrderedIds();
        int idx = selectedIndex;
        if (idx < 0) idx = 0;
        if (idx >= ids.size()) idx = ids.size() - 1;
        return ids.get(idx);
    }

    public int getDisplayAmount() {
        String id = getDisplayId();
        if (id.isEmpty()) return 0;
        return signals.getOrDefault(id, 0);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("SelectedIndex", selectedIndex);
        tag.putLong("LastUpdateTick", lastUpdateTick);

        ListTag list = new ListTag();
        for (Map.Entry<String, Integer> e : signals.entrySet()) {
            CompoundTag s = new CompoundTag();
            s.putString("Id", e.getKey());
            s.putInt("Amount", e.getValue());
            list.add(s);
        }
        tag.put("Signals", list);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        signals.clear();

        if (tag.contains("Signals", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Signals", Tag.TAG_COMPOUND);
            for (Tag t : list) {
                CompoundTag s = (CompoundTag) t;
                String id = s.getString("Id");
                int amount = s.getInt("Amount");
                signals.put(id, amount);
            }
        }

        selectedIndex = tag.getInt("SelectedIndex");
        lastUpdateTick = tag.getLong("LastUpdateTick");

        clampSelectedIndexToSignals();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void onDataPacket(Connection connection,
                             ClientboundBlockEntityDataPacket pkt,
                             HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        loadAdditional(tag, registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.datanessence.display_signal");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new DisplaySignalMenu(id, inv, this);
    }
}