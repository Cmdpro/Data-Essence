package com.cmdpro.datanessence.block.decoration;

import com.cmdpro.datanessence.block.DirectionalPillarBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class PolishedObsidianColumn extends DirectionalPillarBlock {
    public PolishedObsidianColumn(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isPortalFrame(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}
