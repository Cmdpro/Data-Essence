package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.block.world.LunarEssenceCrystal;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.DamageTypeRegistry;
import com.cmdpro.datanessence.registry.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.joml.Math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LunarStrike extends Entity {
    public LunarStrike(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    public int lifetime;

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
    public float size;
    @Override
    public void tick() {
        super.tick();
        lifetime++;
        if (!level().isClientSide) {
            if (lifetime > 50) {
                remove(RemovalReason.DISCARDED);
            }
        }
    }
    public static boolean canSpawnStrikesForPlayer(Player player) {
        return player.getData(AttachmentTypeRegistry.TIER) >= 3;
    }
    public static void strike(Level level, Vec3 pos) {
        BlockPos blockPos = BlockPos.containing(pos);
        LunarStrike strike = new LunarStrike(EntityRegistry.LUNAR_STRIKE.get(), level);
        strike.setPos(pos);
        level.addFreshEntity(strike);
        level.playSound(null, blockPos, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER);
        level.playSound(null, blockPos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER);
        if (level.getBlockState(blockPos).canBeReplaced()) {
            level.setBlockAndUpdate(blockPos, BlockRegistry.LUNAR_CRYSTAL_SEED.get().defaultBlockState());
        }
    }
}
