package com.cmdpro.datanessence.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface INodeUpgrade {
    <T> T getValue(ResourceLocation id, T originalValue, BlockEntity node);
    Type getType();
    enum Type {
        UNIQUE,
        UNIVERSAL
    }
}
