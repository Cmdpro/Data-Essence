package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.registry.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DisplaySignalMenu extends AbstractContainerMenu {

    public static final int BTN_PREV  = 0;
    public static final int BTN_NEXT  = 1;
    public static final int BTN_RESET = 2;

    private final BlockPos pos;
    private final Level level;

    private int selectedIndex;

    public DisplaySignalMenu(int id, Inventory inv, DisplaySignalBlockEntity be) {
        super(MenuRegistry.DISPLAY_SIGNAL_MENU.get(), id);
        this.pos = be.getBlockPos();
        this.level = be.getLevel();
        this.selectedIndex = be.getSelectedIndex();
        addDataSlots();
    }

    public DisplaySignalMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(MenuRegistry.DISPLAY_SIGNAL_MENU.get(), id);
        this.pos = buf.readBlockPos();
        this.level = inv.player.level();
        this.selectedIndex = 0;
        addDataSlots();
    }

    private void addDataSlots() {
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return selectedIndex; }
            @Override public void set(int value) { selectedIndex = value; }
        });
    }

    private DisplaySignalBlockEntity getBE() {
        if (level == null) return null;
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof DisplaySignalBlockEntity d ? d : null;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        DisplaySignalBlockEntity be = getBE();
        if (be == null) return false;

        int newIdx = selectedIndex;
        if (buttonId == BTN_PREV) {
            newIdx--;
        } else if (buttonId == BTN_NEXT) {
            newIdx++;
        } else if (buttonId == BTN_RESET) {
            newIdx = 0;
        } else {
            return false;
        }

        int maxIdx = Math.max(0, be.getSignalCount() - 1);
        if (newIdx < 0) newIdx = 0;
        if (newIdx > maxIdx) newIdx = maxIdx;

        selectedIndex = newIdx;
        be.setSelectedIndex(newIdx);
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level() == level &&
                player.distanceToSqr(pos.getCenter()) <= 64.0D;
    }
}
