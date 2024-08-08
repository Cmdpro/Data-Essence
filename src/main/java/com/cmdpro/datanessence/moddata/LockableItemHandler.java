package com.cmdpro.datanessence.moddata;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;

public class LockableItemHandler extends ItemStackHandler {
    public HashMap<Integer, ItemStack> lockedSlots = new HashMap<>();
    public boolean locked;
    public LockableItemHandler(int size) {
        super(size);
    }

    public LockableItemHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        ListTag locked = new ListTag();
        for (Map.Entry<Integer, ItemStack> i : lockedSlots.entrySet()) {
            CompoundTag tag2 = new CompoundTag();
            tag2.putInt("key", i.getKey());
            tag2.put("value", i.getValue().save(provider));
            locked.add(tag2);
        }
        tag.put("locked", locked);
        tag.putBoolean("isLocked", this.locked);
        return tag;
    }
    public void setLockedSlots() {
        int o = 0;
        for (ItemStack i : stacks) {
            if (!i.isEmpty()) {
                lockedSlots.put(o, i.copy());
            }
            o++;
        }
    }
    public void clearLockedSlots() {
        lockedSlots.clear();
    }
    public boolean canInsertFromBuffer(int slot, ItemStack stack) {
        if (locked) {
            if (lockedSlots.containsKey(slot)) {
                ItemStack locked = lockedSlots.get(slot);
                if (ItemStack.isSameItem(stack, locked)) {
                    return super.isItemValid(slot, stack);
                } else {
                    return false;
                }
            }
        }
        return super.isItemValid(slot, stack);
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        lockedSlots.clear();
        if (nbt.contains("locked")) {
            ListTag locked = (ListTag) nbt.get("locked");
            for (Tag i : locked) {
                CompoundTag tag = (CompoundTag) i;
                lockedSlots.put(tag.getInt("key"), ItemStack.parseOptional(provider, tag.getCompound("value")));
            }
        }
        if (nbt.contains("isLocked")) {
            this.locked = nbt.getBoolean("isLocked");
        }
    }
}
