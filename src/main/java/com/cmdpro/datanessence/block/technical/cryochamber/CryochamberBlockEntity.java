package com.cmdpro.datanessence.block.technical.cryochamber;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// Literally only exists for the renderer
public class CryochamberBlockEntity extends BlockEntity {
    public DatabankAnimationState animState = new DatabankAnimationState("cryosleeping")
            .addAnim(new DatabankAnimationReference("cryosleeping", (state, anim) -> {}, (state, anim) -> {}));

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    public CryochamberBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.CRYOCHAMBER.get(), pos, blockState);
    }
}
