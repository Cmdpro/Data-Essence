package com.cmdpro.datanessence.screen.slot;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class UnuseableSlot extends Slot {
    public UnuseableSlot(int x, int y) {
        super(new SimpleContainer(1), 0, x, y);
    }
    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
}
