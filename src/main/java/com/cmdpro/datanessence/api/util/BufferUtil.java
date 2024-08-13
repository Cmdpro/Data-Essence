package com.cmdpro.datanessence.api.util;

import com.cmdpro.datanessence.api.block.EssenceContainer;
import com.cmdpro.datanessence.block.transmission.EssenceBufferBlockEntity;
import com.cmdpro.datanessence.block.transmission.FluidBufferBlockEntity;
import com.cmdpro.datanessence.block.transmission.ItemBufferBlockEntity;
import com.cmdpro.datanessence.moddata.LockableItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.joml.Math;

public class BufferUtil {
    public static void getEssenceFromBuffersBelow(EssenceContainer container) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof EssenceBufferBlockEntity buffer) {
                EssenceUtil.transferEssence(buffer, container, buffer.getMaxEssence());
                EssenceUtil.transferLunarEssence(buffer, container, buffer.getMaxLunarEssence());
                EssenceUtil.transferNaturalEssence(buffer, container, buffer.getMaxNaturalEssence());
                EssenceUtil.transferExoticEssence(buffer, container, buffer.getMaxExoticEssence());
            } else if (!(ent instanceof ItemBufferBlockEntity || ent instanceof FluidBufferBlockEntity)) {
                break;
            }
        }
    }

    public static void getItemsFromBuffersBelow(BlockEntity container) {
        BufferUtil.getItemsFromBuffersBelow(container, container.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, container.getBlockPos(), Direction.DOWN));
    }

    public static void getItemsFromBuffersBelow(BlockEntity container, IItemHandler handler) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof ItemBufferBlockEntity buffer) {
                IItemHandler resolved = container.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, buffer.getBlockPos(), null);
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
            } else if (!(ent instanceof EssenceBufferBlockEntity || ent instanceof FluidBufferBlockEntity)) {
                break;
            }
        }
    }

    public static void getFluidsFromBuffersBelow(BlockEntity container) {
        BufferUtil.getFluidsFromBuffersBelow(container, container.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, container.getBlockPos(), Direction.DOWN));
    }

    public static void getFluidsFromBuffersBelow(BlockEntity container, IFluidHandler handler) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof FluidBufferBlockEntity buffer) {
                IFluidHandler resolved = container.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, buffer.getBlockPos(), null);
                for (int o = 0; o < resolved.getTanks(); o++) {
                    FluidStack copy = resolved.getFluidInTank(o).copy();
                    if (!copy.isEmpty()) {
                        copy.setAmount(Math.clamp(0, 4000, copy.getAmount()));
                        int filled = handler.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                        resolved.getFluidInTank(o).shrink(filled);
                    }
                }
            } else if (!(ent instanceof EssenceBufferBlockEntity || ent instanceof ItemBufferBlockEntity)) {
                break;
            }
        }
    }
}
