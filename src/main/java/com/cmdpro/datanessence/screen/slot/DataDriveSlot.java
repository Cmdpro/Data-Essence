package com.cmdpro.datanessence.screen.slot;

import com.cmdpro.datanessence.item.DataDrive;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class DataDriveSlot extends SlotItemHandler {
    public DataDriveSlot(IItemHandler itemHandler, int index, int x, int y) {
        super(itemHandler, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof DataDrive;
    }
}