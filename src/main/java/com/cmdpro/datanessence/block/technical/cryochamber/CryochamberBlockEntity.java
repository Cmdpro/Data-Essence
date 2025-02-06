package com.cmdpro.datanessence.block.technical.cryochamber;

import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// Literally only exists for the renderer
public class CryochamberBlockEntity extends BlockEntity {

    public AnimationState animState = new AnimationState();

    public CryochamberBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.CRYOCHAMBER.get(), pos, blockState);
    }
}
