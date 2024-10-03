package com.cmdpro.datanessence.api;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

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
