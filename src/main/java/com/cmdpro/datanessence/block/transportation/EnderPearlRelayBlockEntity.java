package com.cmdpro.datanessence.block.transportation;

import com.cmdpro.datanessence.api.misc.BlockPosNetworks;
import com.cmdpro.datanessence.api.pearlnetwork.PearlNetworkBlockEntity;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnderPearlRelayBlockEntity extends PearlNetworkBlockEntity {
    public AnimationState animState = new AnimationState();
    public EnderPearlRelayBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ENDER_PEARL_RELAY.get(), pos, blockState);
    }
}
