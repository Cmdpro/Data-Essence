package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.util.IDataNEssenceMenuHelper;
import com.cmdpro.datanessence.block.processing.SynthesisChamberBlockEntity;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.MenuRegistry;
import com.cmdpro.datanessence.screen.slot.DataDriveSlot;
import com.cmdpro.datanessence.screen.slot.ModResultSlot;
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

public class SynthesisChamberMenu extends AbstractContainerMenu implements IDataNEssenceMenuHelper {
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return guiHelperQuickMoveStack(player, index, 3, this);
    }
    @Override
    public boolean guiHelperMoveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        return moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
    }
    public final SynthesisChamberBlockEntity blockEntity;
    private final Level level;
    public SynthesisChamberMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    public SynthesisChamberMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(MenuRegistry.SYNTHESIS_CHAMBER_MENU.get(), pContainerId);
        checkContainerSize(inv, 3);
        blockEntity = ((SynthesisChamberBlockEntity) entity);
        this.level = inv.player.level();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null);
        LockableItemHandler handler2 = (LockableItemHandler)blockEntity.getItemHandler();
        this.addSlot(new SlotItemHandler(handler2, 0, 62, 22));
        this.addSlot(new SlotItemHandler(handler2, 1, 62, 45));
        this.addSlot(new ModResultSlot(handler, 2, 116, 34));
        this.addSlot(new DataDriveSlot(handler, 3, 152, 8));
    }

    @Override
    public void slotsChanged(Container pContainer) {
        super.slotsChanged(pContainer);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, BlockRegistry.SYNTHESIS_CHAMBER.get());
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
