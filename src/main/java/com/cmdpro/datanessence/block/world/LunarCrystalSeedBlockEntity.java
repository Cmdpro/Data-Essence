package com.cmdpro.datanessence.block.world;

import com.cmdpro.datanessence.block.technical.StructureProtectorBlockEntity;
import com.cmdpro.datanessence.client.particle.CircleParticleOptionsAdditive;
import com.cmdpro.datanessence.entity.LunarStrike;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.EntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collection;

public class LunarCrystalSeedBlockEntity extends BlockEntity {
    public LunarCrystalSeedBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LUNAR_CRYSTAL_SEED.get(), pos, state);
    }
    public int time;
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.putInt("time", time);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        time = nbt.getInt("time");
        super.loadAdditional(nbt, pRegistries);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, LunarCrystalSeedBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide) {
            pBlockEntity.time++;
            if (pBlockEntity.time >= 120*20) {
                pLevel.removeBlock(pPos, false);
            }
            if (pBlockEntity.time % 40 == 0) {
                if (pLevel.getRandom().nextInt(5) == 0) {
                    for (int i = 0; i < 5; i++) {
                        BlockPos pos = pPos.offset(pLevel.getRandom().nextIntBetweenInclusive(-3, 3), pLevel.getRandom().nextIntBetweenInclusive(-3, 3), pLevel.getRandom().nextIntBetweenInclusive(-3, 3));
                        if (pLevel.getBlockState(pos).canBeReplaced()) {
                            boolean valid = false;
                            BlockState state = BlockRegistry.LUNAR_ESSENCE_CRYSTAL.get().defaultBlockState();
                            Collection<Direction> directionsToTry = Direction.allShuffled(pLevel.getRandom());
                            for (Direction j : directionsToTry) {
                                BlockPos relative = pos.relative(j.getOpposite());
                                if (pLevel.getBlockState(relative).isFaceSturdy(pLevel, relative, j)) {
                                    state = state.setValue(LunarEssenceCrystal.FACING, j);
                                    valid = true;
                                    break;
                                }
                            }
                            if (valid) {
                                pLevel.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS);
                                pLevel.setBlockAndUpdate(pos, state);
                            }
                        }
                    }
                }
            }
        }
    }
}
