package com.cmdpro.datanessence.api.node.item;

import com.cmdpro.datanessence.api.node.NodePathEnd;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public interface INodeUpgrade {
    default Object getValue(ResourceLocation id, Object originalValue, BlockEntity node) { return null; };
    default boolean preTransfer(BlockEntity from, List<NodePathEnd> other, boolean canceled) { return canceled; }
    default void postTransfer(BlockEntity from, List<NodePathEnd> other) {}
    Type getType();
    enum Type {
        UNIQUE,
        UNIVERSAL
    }
}
