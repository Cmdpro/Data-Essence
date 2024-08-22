package com.cmdpro.datanessence.multiblock;

import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MultiblockPredicate {
    public abstract boolean isSame(BlockState other, Rotation rotation);
    public abstract MultiblockPredicateSerializer getSerializer();
    public abstract BlockState getVisual();
}
