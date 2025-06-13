package com.cmdpro.datanessence.block.transportation;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.api.misc.BlockPosNetworks;
import com.cmdpro.datanessence.api.pearlnetwork.PearlNetworkBlockEntity;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnderPearlRelayBlockEntity extends PearlNetworkBlockEntity {
    public DatabankAnimationState animState = new DatabankAnimationState("idle")
            .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}));

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    public EnderPearlRelayBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ENDER_PEARL_RELAY.get(), pos, blockState);
    }
}
