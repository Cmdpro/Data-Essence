package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.screen.slot.DataDriveSlot;
import com.cmdpro.datanessence.screen.slot.ModResultSlot;
import com.cmdpro.datanessence.util.IDataNEssenceMenuHelper;
import com.cmdpro.datanessence.block.processing.AutoFabricatorBlockEntity;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class AutoFabricatorMenu extends AbstractContainerMenu implements IDataNEssenceMenuHelper {
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return guiHelperQuickMoveStack(player, index, 11, this);
    }
    @Override
    public boolean guiHelperMoveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        return moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
    }
    public final AutoFabricatorBlockEntity blockEntity;
    private final Level level;
    public AutoFabricatorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    public AutoFabricatorMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(MenuRegistry.AUTO_FABRICATOR_MENU.get(), pContainerId);
        blockEntity = ((AutoFabricatorBlockEntity) entity);
        this.level = inv.player.level();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        int i, j;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), j + (i * 3), 54 + (j * 18), 17 + (i * 18)));
            }
        }
        this.addSlot(new ModResultSlot(blockEntity.getOutputHandler(), 0, 145, 35));
        this.addSlot(new DataDriveSlot(blockEntity.getDataDriveHandler(), 0, 152, 4));
    }

    @Override
    public void slotsChanged(Container pContainer) {
        super.slotsChanged(pContainer);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, BlockRegistry.AUTO_FABRICATOR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

}
