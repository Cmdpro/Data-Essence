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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

public class LimitedItemBufferBlockEntity extends ItemBufferBlockEntity {
    public LimitedItemBufferBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.LIMITED_ITEM_BUFFER.get(), pPos, pBlockState);
    }

    @Override
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
                    int limit = 2-handler.getStackInSlot(p).getCount();
                    if (limit <= 0) {
                        p++;
                        continue;
                    }
                    copyCopy.setCount(Math.clamp(0, limit, copyCopy.getCount()));
                    boolean canInsert = true;
                    if (handler instanceof LockableItemHandler lockable) {
                        canInsert = lockable.canInsertFromBuffer(p, copyCopy);
                    }
                    if (canInsert) {
                        int remove = handler.insertItem(p, copyCopy, false).getCount()+(copy.getCount()-copyCopy.getCount());
                        if (remove < copy.getCount()) {
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
}
