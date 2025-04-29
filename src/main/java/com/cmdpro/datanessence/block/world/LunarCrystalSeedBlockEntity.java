package com.cmdpro.datanessence.block.world;

import com.cmdpro.datanessence.block.technical.StructureProtectorBlockEntity;
import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.entity.LunarStrike;
import com.cmdpro.datanessence.registry.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        } else {
            pBlockEntity.wispTimer--;
            if (pBlockEntity.wispTimer <= 0) {
                pBlockEntity.wispTimer = pLevel.getRandom().nextIntBetweenInclusive(5, 25);
                int wisps = pLevel.getRandom().nextIntBetweenInclusive(1, 2);
                for (int i = 0; i < wisps; i++) {
                    pBlockEntity.wisps.add(LunarCrystalSeedWisp.create(pLevel.getRandom()));
                }
            }
            List<LunarCrystalSeedWisp> wispsToRemove = new ArrayList<>();
            for (LunarCrystalSeedWisp i : pBlockEntity.wisps) {
                i.tick(pLevel, pPos.getCenter());
                if (i.shouldDelete()) {
                    wispsToRemove.add(i);
                }
            }
            for (LunarCrystalSeedWisp i : wispsToRemove) {
                pBlockEntity.wisps.remove(i);
            }
        }
    }
    public int wispTimer;
    public List<LunarCrystalSeedWisp> wisps = new ArrayList<>();
    public static class LunarCrystalSeedWisp {
        public static final float speedMult = 10;
        private RandomSource random;
        public int lifetime;
        public int initialLifetime;
        public Vec3 offset;
        public Vec3 motion;
        public int timeUntilRedirect;
        private boolean deletionQueued;
        public void tick(Level level, Vec3 center) {
            timeUntilRedirect--;
            if (timeUntilRedirect <= 0) {
                motion = new Vec3(random.nextFloat()-0.5, random.nextFloat()-0.5, random.nextFloat()-0.5).normalize().scale(speedMult);
                timeUntilRedirect = random.nextIntBetweenInclusive(5, 10);
            }
            for (int i = 0; i < 5; i++) {
                offset = offset.add(motion.scale(1f / 20f).scale(1f/5f));
                addParticles(level, center);
            }
            lifetime--;
            if (lifetime <= 0) {
                queueDeletion();
            }
        }
        public void addParticles(Level level, Vec3 center) {
            Vec3 vec = center.add(offset);
            level.addParticle(new CircleParticleOptions().setColor(new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color)).setAdditive(true), vec.x, vec.y, vec.z, 0, 0, 0);
        }
        public void queueDeletion() {
            deletionQueued = true;
        }
        public boolean shouldDelete() {
            return deletionQueued;
        }
        public static LunarCrystalSeedWisp create(RandomSource random) {
            LunarCrystalSeedWisp wisp = new LunarCrystalSeedWisp();
            wisp.initialLifetime = 150;
            wisp.lifetime = wisp.initialLifetime;
            wisp.offset = new Vec3(0, 0, 0);
            wisp.random = random;
            return wisp;
        }
    }
}
