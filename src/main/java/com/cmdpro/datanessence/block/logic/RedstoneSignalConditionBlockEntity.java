package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RedstoneSignalConditionBlockEntity extends AbstractSignalConditionBlockEntity {

    public RedstoneSignalConditionBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.REDSTONE_SIGNAL_CONDITION.get(), pos, state);
    }

    @Override
    protected void onConditionUpdated(boolean active) {
        if (level == null) return;
        BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(BlockStateProperties.POWERED)) {
            if (state.getValue(BlockStateProperties.POWERED) != active) {
                level.setBlock(worldPosition, state.setValue(BlockStateProperties.POWERED, active), 1);
            }
        }
    }
}
