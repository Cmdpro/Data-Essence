package com.cmdpro.datanessence.api.node.item;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public interface INodeUpgrade {
    default Object getValue(ItemStack upgrade, ResourceLocation id, Object originalValue, BlockEntity node) { return null; };
    default boolean preTransfer(ItemStack upgrade, BlockEntity from, List<GraphPath<BlockPos, DefaultEdge>> other, boolean canceled) { return canceled; }
    default void postTransfer(ItemStack upgrade, BlockEntity from, List<GraphPath<BlockPos, DefaultEdge>> other) {}
    Type getType();
    enum Type {
        UNIQUE,
        UNIVERSAL
    }
}
