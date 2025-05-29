package com.cmdpro.datanessence.block.world;

import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.ParticleBurst;
import com.cmdpro.datanessence.registry.*;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LunarCrystalSeedBlockEntity extends BlockEntity {
    public LunarCrystalSeedBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LUNAR_CRYSTAL_SEED.get(), pos, state);
    }
    public int crystalSpawnTime;
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.putInt("crystalSpawnTime", crystalSpawnTime);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        crystalSpawnTime = nbt.getInt("crystalSpawnTime");
        super.loadAdditional(nbt, pRegistries);
    }
    public void resetCrystalSpawnTime() {
        crystalSpawnTime = level.getRandom().nextIntBetweenInclusive(6*20, 8*20);
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, LunarCrystalSeedBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide) {
            if (pBlockEntity.crystalSpawnTime <= 0) {
                pBlockEntity.resetCrystalSpawnTime();
            }
            pBlockEntity.crystalSpawnTime--;
            if (pLevel.isDay() && pLevel.getBrightness(LightLayer.SKY, pPos) > 3) {
                ModMessages.sendToPlayersNear(new ParticleBurst(pPos.getCenter(), new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color), 100, 0.75f), (ServerLevel)pLevel, pPos.getCenter(), 64);
                pLevel.removeBlock(pPos, false);
            }
            if (pBlockEntity.crystalSpawnTime <= 0) {
                pBlockEntity.resetCrystalSpawnTime();
                List<BlockPos> locations = new ArrayList<>();
                for (int x = -3; x <= 3; x++) {
                    for (int y = -3; y <= 3; y++) {
                        for (int z = -3; z <= 3; z++) {
                            BlockPos blockPos = new BlockPos(x, y, z);
                            if (pLevel.getBlockState(blockPos.offset(pPos)).canBeReplaced()) {
                                locations.add(blockPos);
                            }
                        }
                    }
                }
                Util.shuffle(locations, pLevel.getRandom());
                for (BlockPos i : locations) {
                    BlockPos pos = pPos.offset(i);
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
                        ModMessages.sendToPlayersNear(new ParticleBurst(pos.getCenter(), new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color), 25, 0.1f), (ServerLevel)pLevel, pPos.getCenter(), 64);
                        pLevel.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS);
                        pLevel.setBlockAndUpdate(pos, state);
                        break;
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
