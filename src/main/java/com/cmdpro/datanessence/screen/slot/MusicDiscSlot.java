package com.cmdpro.datanessence.screen.slot;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MusicDiscSlot extends SlotItemHandler {

    public MusicDiscSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.has(DataComponents.JUKEBOX_PLAYABLE);
    }
}
