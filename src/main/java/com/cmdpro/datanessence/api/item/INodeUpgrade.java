package com.cmdpro.datanessence.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface INodeUpgrade {
    default Object getValue(ResourceLocation id, Object originalValue, BlockEntity node) { return null; };
    default boolean preTransfer(BlockEntity node, BlockEntity other, boolean canceled) { return canceled; }
    default boolean preTake(BlockEntity node, BlockEntity other, boolean canceled) { return canceled; }
    default void postTransfer(BlockEntity node, BlockEntity other) {}
    default void postTake(BlockEntity node, BlockEntity other) {}
    Type getType();
    enum Type {
        UNIQUE,
        UNIVERSAL
    }
}
