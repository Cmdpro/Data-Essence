package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

public class ItemBufferBlockEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = createItemHandler();
    public ItemStackHandler createItemHandler() {
        return new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }
    public ItemBufferBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.ITEM_BUFFER.get(), pPos, pBlockState);
    }
    public ItemBufferBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    public void transfer(IItemHandler handler) {
        IItemHandler resolved = getItemHandler();
        boolean movedAnything = false;
        for (int o = 0; o < resolved.getSlots(); o++) {
            ItemStack copy = resolved.getStackInSlot(o).copy();
            if (!copy.isEmpty()) {
                copy.setCount(Math.clamp(0, 16, copy.getCount()));
                ItemStack copy2 = copy.copy();
                int p = 0;
                while (p < handler.getSlots()) {
                    ItemStack copyCopy = copy.copy();
                    boolean canInsert = true;
                    if (handler instanceof LockableItemHandler lockable) {
                        canInsert = lockable.canInsertFromBuffer(p, copyCopy);
                    }
                    if (canInsert) {
                        int remove = handler.insertItem(p, copyCopy, false).getCount();
                        if (remove < copyCopy.getCount()) {
                            movedAnything = true;
                        }
                        copy.setCount(remove);
                        if (remove <= 0) {
                            break;
                        }
                    }
                    p++;
                }
                if (movedAnything) {
                    resolved.extractItem(o, copy2.getCount() - copy.getCount(), false);
                    break;
                }
            }
        }
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
    }
    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    public IItemHandler getItemHandler() {
        return lazyItemHandler.get();
    }
    private Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        return inventory;
    }
}
