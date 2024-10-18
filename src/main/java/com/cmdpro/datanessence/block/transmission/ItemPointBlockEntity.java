package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.api.misc.ICustomFluidPointBehaviour;
import com.cmdpro.datanessence.api.misc.ICustomItemPointBehaviour;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.awt.*;

public class ItemPointBlockEntity extends BaseCapabilityPointBlockEntity {
    public ItemPointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ITEM_POINT.get(), pos, state);
    }
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public IItemHandler getItemHandler() {
        return lazyItemHandler.get();
    }
    private Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);
    @Override
    public Color linkColor() {
        return new Color(0xef4d3d);
    }
    @Override
    public void transfer(BlockEntity other) {
        IItemHandler resolved2 = level.getCapability(Capabilities.ItemHandler.BLOCK, other.getBlockPos(), getDirection());
        if (resolved2 == null) {
            return;
        }
        if (resolved2.getStackInSlot(0).getCount() >= getFinalSpeed(DataNEssenceConfig.itemPointTransfer)) {
            return;
        }
        deposit(other);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        super.saveAdditional(tag, pRegistries);
    }

    @Override
    public void deposit(BlockEntity other) {
        IItemHandler resolved = level.getCapability(Capabilities.ItemHandler.BLOCK, other.getBlockPos(), getDirection());
        IItemHandler resolved2 = level.getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos(), null);
        if (resolved == null || resolved2 == null) {
            return;
        }
        if (other instanceof ICustomItemPointBehaviour behaviour) {
            if (!behaviour.canInsertItem(resolved, resolved2)) {
                return;
            }
        }
        boolean movedAnything = false;
        for (int o = 0; o < resolved2.getSlots(); o++) {
            ItemStack copy = resolved2.getStackInSlot(o).copy();
            if (!copy.isEmpty()) {
                copy.setCount(Math.clamp(0, DataNEssenceConfig.itemPointTransfer, copy.getCount()));
                ItemStack copy2 = copy.copy();
                int p = 0;
                while (p < resolved.getSlots()) {
                    ItemStack copyCopy = copy.copy();
                    int remove = resolved.insertItem(p, copyCopy, false).getCount();
                    if (remove < copyCopy.getCount()) {
                        movedAnything = true;
                    }
                    copy.setCount(remove);
                    if (remove <= 0) {
                        break;
                    }
                    p++;
                }
                if (movedAnything) {
                    resolved2.extractItem(o, copy2.getCount() - copy.getCount(), false);
                    break;
                }
            }
        }
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
    }
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        return inventory;
    }
    public void drops() {
        SimpleContainer inventory = getInv();
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public void take(BlockEntity other) {
        IItemHandler resolved = level.getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos(), null);
        IItemHandler resolved2 = level.getCapability(Capabilities.ItemHandler.BLOCK, other.getBlockPos(), getDirection());
        if (resolved == null || resolved2 == null) {
            return;
        }
        if (resolved.getStackInSlot(0).getCount() >= getFinalSpeed(DataNEssenceConfig.itemPointTransfer)) {
            return;
        }
        if (other instanceof ICustomItemPointBehaviour behaviour) {
            if (!behaviour.canExtractItem(resolved2, resolved)) {
                return;
            }
        }
        boolean movedAnything = false;
        for (int o = 0; o < resolved2.getSlots(); o++) {
            ItemStack copy = resolved2.getStackInSlot(o).copy();
            if (!copy.isEmpty()) {
                copy.setCount(Math.clamp(0, DataNEssenceConfig.itemPointTransfer, copy.getCount()));
                ItemStack copy2 = copy.copy();
                int p = 0;
                while (p < resolved.getSlots()) {
                    ItemStack copyCopy = copy.copy();
                    int remove = resolved.insertItem(p, copyCopy, false).getCount();
                    if (remove < copyCopy.getCount()) {
                        movedAnything = true;
                    }
                    copy.setCount(remove);
                    if (remove <= 0) {
                        break;
                    }
                    p++;
                }
                if (movedAnything) {
                    resolved2.extractItem(o, copy2.getCount() - copy.getCount(), false);
                    break;
                }
            }
        }
    }
}
