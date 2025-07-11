package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.MenuRegistry;
import com.cmdpro.datanessence.screen.slot.UnclickableInventorySlot;
import com.cmdpro.datanessence.screen.slot.UnuseableSlot;
import com.cmdpro.datanessence.util.IDataNEssenceMenuHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.List;

public class FilterNodeUpgradeMenu extends AbstractContainerMenu implements IDataNEssenceMenuHelper {
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return guiHelperQuickMoveStack(player, index, 1, this);
    }
    @Override
    public boolean guiHelperMoveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        return moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
    }
    public final ItemStack stack;
    private final Level level;
    public FilterNodeUpgradeMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.getSlot(extraData.readInt()).get());
    }
    Slot slot;
    public FilterNodeUpgradeMenu(int pContainerId, Inventory inv, ItemStack stack) {
        super(MenuRegistry.FILTER_NODE_UPGRADE_MENU.get(), pContainerId);
        this.stack = stack;
        this.level = inv.player.level();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        slot = this.addSlot(new UnuseableSlot(80, 34));
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        super.clicked(slotId, button, clickType, player);
        if (slotId == slots.indexOf(slot)) {
            if (clickType.equals(ClickType.PICKUP) || clickType.equals(ClickType.SWAP)) {
                ItemStack carried = getCarried();
                stack.set(DataComponentRegistry.FILTER_STACK.get(), ItemContainerContents.fromItems(List.of(carried)));
            }
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                int slot = l + i * 9 + 9;
                if (playerInventory.getItem(slot) == stack) {
                    this.addSlot(new UnclickableInventorySlot(playerInventory, slot, 8 + l * 18, 84 + i * 18));
                } else {
                    this.addSlot(new Slot(playerInventory, slot, 8 + l * 18, 84 + i * 18));
                }
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            if (playerInventory.getItem(i) == stack) {
                this.addSlot(new UnclickableInventorySlot(playerInventory, i, 8 + i * 18, 142));
            } else {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
            }
        }
    }

}
