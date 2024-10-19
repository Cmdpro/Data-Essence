package com.cmdpro.datanessence.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public interface INodeUpgrade {
    default Object getValue(ResourceLocation id, Object originalValue, BlockEntity node) { return null; };
    default boolean preTransfer(BlockEntity from, List<BlockEntity> other, boolean canceled) { return canceled; }
    default void postTransfer(BlockEntity from, List<BlockEntity> other) {}
    Type getType();
    enum Type {
        UNIQUE,
        UNIVERSAL
    }
}
