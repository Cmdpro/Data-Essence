package com.cmdpro.datanessence.block.transportation;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.api.misc.BlockPosNetworks;
import com.cmdpro.datanessence.api.pearlnetwork.PearlNetworkBlockEntity;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public class EnderPearlDestinationBlockEntity extends PearlNetworkBlockEntity {public DatabankAnimationState animState = new DatabankAnimationState("idle")
        .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}));

    public EnderPearlDestinationBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ENDER_PEARL_DESTINATION.get(), pos, blockState);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    @Override
    public Vec3 getLinkShift() {
        return new Vec3(0, -0.5, 0);
    }
}
