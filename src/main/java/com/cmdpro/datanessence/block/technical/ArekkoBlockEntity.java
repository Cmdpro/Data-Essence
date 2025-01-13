package com.cmdpro.datanessence.block.technical;

import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// Literally only exists for the renderer
public class ArekkoBlockEntity extends BlockEntity {

    public AnimationState animState = new AnimationState();

    public ArekkoBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.AREKKO.get(), pos, blockState);
    }
}
