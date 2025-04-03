package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.api.node.ICustomItemPointBehaviour;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
    public void transfer(BaseCapabilityPointBlockEntity from, List<GraphPath<BlockPos, DefaultEdge>> other) {
        int transferAmount = (int)Math.floor((float)getFinalSpeed(DataNEssenceConfig.itemPointTransfer)/(float)other.size());
        for (GraphPath<BlockPos, DefaultEdge> i : other) {
            if (level.getBlockEntity(i.getEndVertex()) instanceof BaseCapabilityPointBlockEntity ent) {
                List<ItemStack> allowedItemstacks = null;
                for (BlockPos j : i.getVertexList()) {
                    if (level.getBlockEntity(j) instanceof BaseCapabilityPointBlockEntity ent2) {
                        List<ItemStack> value = ent2.getValue(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "allowed_itemstacks"), null);
                        if (allowedItemstacks == null) {
                            allowedItemstacks = value;
                        } else if (value != null) {
                            allowedItemstacks = allowedItemstacks.stream().filter((stack1) -> value.stream().anyMatch((stack2) -> ItemStack.isSameItem(stack1, stack2))).toList();
                        }
                    }
                }
                IItemHandler resolved = level.getCapability(Capabilities.ItemHandler.BLOCK, ent.getBlockPos().relative(ent.getDirection().getOpposite()), ent.getDirection());
                IItemHandler resolved2 = level.getCapability(Capabilities.ItemHandler.BLOCK, from.getBlockPos().relative(from.getDirection().getOpposite()), from.getDirection());
                if (resolved == null || resolved2 == null) {
                    continue;
                }
                if (level.getBlockEntity(from.getBlockPos().relative(from.getDirection().getOpposite())) instanceof ICustomItemPointBehaviour behaviour) {
                    if (!behaviour.canExtractItem(resolved, resolved2)) {
                        continue;
                    }
                }
                if (level.getBlockEntity(ent.getBlockPos().relative(ent.getDirection().getOpposite())) instanceof ICustomItemPointBehaviour behaviour) {
                    if (!behaviour.canInsertItem(resolved, resolved2)) {
                        continue;
                    }
                }
                boolean movedAnything = false;
                for (int o = 0; o < resolved2.getSlots(); o++) {
                    ItemStack copy = resolved2.extractItem(o, transferAmount, true).copy();
                    if (allowedItemstacks != null && allowedItemstacks.stream().noneMatch((stack) -> ItemStack.isSameItem(stack, copy))) {
                        continue;
                    }
                    if (!copy.isEmpty()) {
                        ItemStack copy2 = copy.copy();
                        int p = 0;
                        while (p < resolved.getSlots()) {
                            ItemStack copyCopy = copy.copy();
                            int remaining = resolved.insertItem(p, copyCopy, false).getCount();
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
                            resolved2.extractItem(o, copy2.getCount() - copy.getCount(), false);
                            break;
                        }
                    }
                }
            }
        }
    }
}
