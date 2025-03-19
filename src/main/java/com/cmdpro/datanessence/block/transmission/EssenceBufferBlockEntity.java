package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.MultiEssenceContainer;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class EssenceBufferBlockEntity extends BlockEntity implements EssenceBlockEntity {
    public MultiEssenceContainer storage = new MultiEssenceContainer(List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), EssenceTypeRegistry.EXOTIC_ESSENCE.get()), 1000);
    @Override
    public EssenceStorage getStorage() {
        return storage;
    }
    public EssenceBufferBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.ESSENCE_BUFFER.get(), pPos, pBlockState);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("EssenceStorage", storage.toNbt());
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
    }

    public void doTransferParticles(BlockPos pos, Level world) {
        RandomSource random = world.getRandom();
        Color color = new Color(162, 29, 220);
        double d0 = 0.5625;

        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            if (!world.getBlockState(blockpos).isSolidRender(world, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5 + d0 * (double)direction.getStepX() : (double)random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5 + d0 * (double)direction.getStepY() : (double)random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5 + d0 * (double)direction.getStepZ() : (double)random.nextFloat();
                world.addParticle(
                        new MoteParticleOptions(color, true, 20), (double)pos.getX() + d1, (double)pos.getY() + d2, (double)pos.getZ() + d3, 0.0, 0.1, 0.0
                );
            }
        }
    }
}
