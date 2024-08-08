package com.cmdpro.datanessence.moddata;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;

public class TrueLockableItemHandler extends LockableItemHandler {
    public TrueLockableItemHandler(int size) {
        super(size);
    }

    public TrueLockableItemHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return super.canInsertFromBuffer(slot, stack);
    }
}
