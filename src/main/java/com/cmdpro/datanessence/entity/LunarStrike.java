package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.block.world.LunarEssenceCrystal;
import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.registry.*;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.joml.Math;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LunarStrike extends Entity {
    public LunarStrike(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public LunarStrike(EntityType<?> pEntityType, Vec3 pos, Level pLevel) {
        this(pEntityType, pLevel);
        changeStrikePos(pos);
    }
    public void changeStrikePos(Vec3 pos) {
        targetPos = pos;
        getEntityData().set(DIRECTION, (float)getRandom().nextIntBetweenInclusive(-180, 180));
        startPos = pos.add(Vec3.directionFromRotation(getRandom().nextIntBetweenInclusive(-70, -60), getEntityData().get(DIRECTION)).scale(maxLifetime*2));
        setPos(startPos);
        lifetime = 0;
    }
    public static final EntityDataAccessor<Float> DIRECTION = SynchedEntityData.defineId(LunarStrike.class, EntityDataSerializers.FLOAT);
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DIRECTION, 0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }
    public final int maxLifetime = 50;
    public int lifetime;
    public Vec3 targetPos;
    public Vec3 startPos;
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            if (targetPos == null || startPos == null) {
                changeStrikePos(position());
            }
        }
        super.tick();
        lifetime++;
        if (!level().isClientSide) {
            setDeltaMovement(startPos.vectorTo(targetPos).normalize().scale(startPos.distanceTo(targetPos)/(float)maxLifetime));
            setPos(startPos.lerp(targetPos, (float)lifetime/(float)maxLifetime));
            if (lifetime > maxLifetime) {
                hit();
                remove(RemovalReason.DISCARDED);
            }
        } else {
            for (int i = 0; i <= 5; i++) {
                Vec3 vec = getBoundingBox().getCenter().add(getDeltaMovement().scale((1f/5f)*(float)i));
                level().addParticle(new CircleParticleOptions().setColor(new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color)).setAdditive(true), vec.x, vec.y, vec.z, 0, 0, 0);
            }
            setPos(position().add(getDeltaMovement()));
        }
    }
    public static boolean canSpawnStrikesForPlayer(Player player) {
        return player.getData(AttachmentTypeRegistry.TIER) >= 3;
    }
    public void hit() {
        level().playSound(null, blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER);
        level().playSound(null, blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER);
        for (int i = 0; i < 10; i++) {
            BlockPos blockPos = blockPosition();
            blockPos.offset(getRandom().nextIntBetweenInclusive(-3, 3), 0, getRandom().nextIntBetweenInclusive(-3, 3));
            int shift = 0;
            while (!level().getBlockState(blockPos).canBeReplaced() && shift < 4) {
                blockPos = blockPos.offset(0, 1, 0);
                shift++;
            }
            while (level().getBlockState(blockPos).canBeReplaced() && shift > -4) {
                blockPos = blockPos.offset(0, -1, 0);
                shift--;
            }
            blockPos = blockPos.offset(0, 1, 0);
            if (level().getBlockState(blockPos).canBeReplaced() && level().getBlockState(blockPos.below()).isFaceSturdy(level(), blockPos.below(), Direction.UP)) {
                level().setBlockAndUpdate(blockPos, BlockRegistry.LUNAR_CRYSTAL_SEED.get().defaultBlockState());
                break;
            }
        }
    }
}
