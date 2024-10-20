package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.node.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.api.misc.ICustomItemPointBehaviour;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.joml.Math;

import java.awt.*;
import java.util.List;

public class ItemPointBlockEntity extends BaseCapabilityPointBlockEntity {
    public ItemPointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ITEM_POINT.get(), pos, state);
    }
    @Override
    public Color linkColor() {
        return new Color(0xef4d3d);
    }
    @Override
    public void transfer(BaseCapabilityPointBlockEntity from, List<BaseCapabilityPointBlockEntity> other) {
        int transferAmount = (int)Math.floor((float)getFinalSpeed(DataNEssenceConfig.itemPointTransfer)/(float)other.size());
        for (BaseCapabilityPointBlockEntity i : other) {
            IItemHandler resolved = level.getCapability(Capabilities.ItemHandler.BLOCK, i.getBlockPos().relative(i.getDirection().getOpposite()), i.getDirection());
            IItemHandler resolved2 = level.getCapability(Capabilities.ItemHandler.BLOCK, from.getBlockPos().relative(from.getDirection().getOpposite()), from.getDirection());
            if (resolved == null || resolved2 == null) {
                continue;
            }
            if (resolved == null || resolved2 == null) {
                continue;
            }
            if (other instanceof ICustomItemPointBehaviour behaviour) {
                if (!behaviour.canInsertItem(resolved, resolved2)) {
                    continue;
                }
            }
            boolean movedAnything = false;
            for (int o = 0; o < resolved2.getSlots(); o++) {
                ItemStack copy = resolved2.getStackInSlot(o).copy();
                if (!copy.isEmpty()) {
                    copy.setCount(Math.clamp(0, transferAmount, copy.getCount()));
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
}
