package com.cmdpro.datanessence.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface GenderfluidGrowable {
    /**
     * Induce growth in this plant.
     * @param state block state to operate on
     * @param world world to operate in
     * @param pos coords of the block
     * @param random random source
     * @param chance how often the growth may occur (works inverse of what one may intuit - smaller number is more frequent!)
     */
    void grow(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, int chance);
}
