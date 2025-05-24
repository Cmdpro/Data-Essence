package com.cmdpro.datanessence.block.decoration;

import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// Literally only exists for the renderer
public class EssenceBridgeBlockEntity extends BlockEntity {
    public EssenceBridgeBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ESSENCE_BRIDGE.get(), pos, blockState);
    }
}
