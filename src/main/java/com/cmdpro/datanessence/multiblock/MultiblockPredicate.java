package com.cmdpro.datanessence.multiblock;

import net.minecraft.world.level.block.state.BlockState;

public abstract class MultiblockPredicate {
    public abstract boolean isSame(BlockState other);
    public abstract MultiblockPredicateSerializer getSerializer();
}
