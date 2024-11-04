package com.cmdpro.datanessence.block.production;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EssenceBreakerBlockEntity extends BlockEntity implements EssenceBlockEntity {
    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 500);
    private final float breakCost = 25f;
    public int interval;

    public EssenceBreakerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.ESSENCE_BREAKER.get(), pPos, pBlockState);
    }

    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    public static void tick(Level world, BlockPos pos, BlockState state, EssenceBreakerBlockEntity tile) {

        if (!world.isClientSide()) {
            if (tile.interval <= 0) {
                if (!world.hasNeighborSignal(pos) && tile.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()) >= tile.breakCost) {
                    BlockPos breakPos = pos.relative(state.getValue(EssenceBreaker.FACING));

                    world.destroyBlock(breakPos, true);
                    world.addParticle(ParticleTypes.EXPLOSION, breakPos.getX(), breakPos.getY(), breakPos.getZ(), 1.0, 1.0, 1.0);

                    tile.interval = 20;
                }
            }
            else {
                tile.interval--;
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("BreakInterval", interval);
        super.saveAdditional(tag, provider);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.Provider provider) {
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        interval = tag.getInt("BreakInterval");
        super.loadAdditional(tag, provider);
    }
}
