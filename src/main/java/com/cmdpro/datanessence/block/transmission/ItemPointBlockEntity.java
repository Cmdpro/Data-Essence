package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.api.node.ICustomItemPointBehaviour;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
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
    public void transfer(BaseCapabilityPointBlockEntity sourceNode, List<GraphPath<BlockPos, DefaultEdge>> other) {
        int transferAmount = (int)Math.floor((float)getFinalSpeed(DataNEssenceConfig.itemPointTransfer)/(float)other.size());
        for (GraphPath<BlockPos, DefaultEdge> i : other) {
            if (level.getBlockEntity(i.getEndVertex()) instanceof BaseCapabilityPointBlockEntity destinationNode) {
                List<ItemStack> allowedItemstacks = null;
                var sourceTile = sourceNode.getBlockPos().relative(sourceNode.getDirection().getOpposite());
                var destTile = destinationNode.getBlockPos().relative(destinationNode.getDirection().getOpposite());
                IItemHandler sourceHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, sourceTile, sourceNode.getDirection());
                IItemHandler destHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, destTile, destinationNode.getDirection());

                for (BlockPos j : i.getVertexList()) {
                    if (level.getBlockEntity(j) instanceof BaseCapabilityPointBlockEntity ent2) {
                        List<ItemStack> value = ent2.getValue(DataNEssence.locate("allowed_itemstacks"), null);
                        if (allowedItemstacks == null) {
                            allowedItemstacks = value;
                        } else if (value != null) {
                            allowedItemstacks = allowedItemstacks.stream().filter((stack1) -> value.stream().anyMatch((stack2) -> ItemStack.isSameItem(stack1, stack2))).toList();
                        }
                    }
                }

                if (destHandler == null || sourceHandler == null) {
                    continue;
                }

                if (level.getBlockEntity(sourceTile) instanceof ICustomItemPointBehaviour behaviour) {
                    if (!behaviour.canExtractItem(destHandler, sourceHandler)) {
                        continue;
                    }
                }

                if (level.getBlockEntity(destTile) instanceof ICustomItemPointBehaviour behaviour) {
                    if (!behaviour.canInsertItem(destHandler, sourceHandler)) {
                        continue;
                    }
                }

                if (isDestinationFull(destHandler))
                    continue;

                moveItems(sourceHandler, destHandler, transferAmount, allowedItemstacks);
            }
        }
    }

    // the way this is coded feels inelegant. but we really shouldn't be doing any work if the current destination container is full
    public boolean isDestinationFull(IItemHandler destHandler) {
        int fullSlots = 0;
        for (int slot = 0; slot < destHandler.getSlots(); slot++) {
            if ( destHandler.getStackInSlot(slot).getCount() >= destHandler.getSlotLimit(slot) )
                fullSlots++;
        }
        return fullSlots >= destHandler.getSlots();
    }

    public void moveItems(IItemHandler sourceHandler, IItemHandler destHandler, int transferAmount, List<ItemStack> allowedItemstacks) {
        boolean movedAnything = false;

        for (int o = 0; o < sourceHandler.getSlots(); o++) {
            ItemStack copy = sourceHandler.extractItem(o, transferAmount, true).copy();
            if (allowedItemstacks != null && allowedItemstacks.stream().noneMatch((stack) -> ItemStack.isSameItem(stack, copy))) {
                continue;
            }
            if (!copy.isEmpty()) {
                ItemStack copy2 = copy.copy();
                int p = 0;
                while (p < destHandler.getSlots()) {
                    ItemStack copyCopy = copy.copy();
                    int remaining = destHandler.insertItem(p, copyCopy, false).getCount();
                    copy.setCount(remaining);
                    if (copy2.getCount() - copy.getCount() > 0) {
                        movedAnything = true;
                    }
                    if (remaining <= 0) {
                        break;
                    }
                    p++;
                }
                if (movedAnything) {
                    sourceHandler.extractItem(o, copy2.getCount() - copy.getCount(), false);
                    break;
                }
            }
        }
    }
}
