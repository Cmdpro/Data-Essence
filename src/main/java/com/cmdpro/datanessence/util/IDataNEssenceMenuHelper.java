package com.cmdpro.datanessence.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface IDataNEssenceMenuHelper {
    boolean guiHelperMoveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection);
    default ItemStack guiHelperQuickMoveStack(Player player, int index, int slotCount, AbstractContainerMenu menu) {
        NonNullList<Slot> slots = menu.slots;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ItemStack copy = stack.copy();

            if (index < 36) {
                if (!guiHelperMoveItemStackTo(stack, 36, 36 + slotCount, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 36 + slots.size()) {
                if (!guiHelperMoveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
            slot.set(stack);
            slot.onTake(player, stack);
            return copy;
        } else {
            return ItemStack.EMPTY;
        }
    }
}
