package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.EntityRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.datafix.fixes.EntityIdFix;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.joml.Math;

public class BlackHole extends Entity {
    public BlackHole(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(BlackHole.class, EntityDataSerializers.FLOAT);
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(SIZE, 8f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        entityData.set(SIZE, pCompound.getFloat("size"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("size", entityData.get(SIZE));
    }
    @Override
    public void tick() {
        super.tick();
        for (Entity i : level().getEntitiesOfClass(Entity.class, AABB.ofSize(getBoundingBox().getCenter(), entityData.get(SIZE)*2.5f, entityData.get(SIZE)*2.5f, entityData.get(SIZE)*2.5f))) {
            if (i != this && !i.getType().is(Tags.EntityTypes.BOSSES) && !i.getType().equals(EntityRegistry.BLACK_HOLE.get())) {
                if (i instanceof Player) {
                    i.hurtMarked = true;
                }
                float mult = Math.clamp(0, 0.5f, (entityData.get(SIZE)*2.5f)-i.distanceTo(this));
                Vec3 add = getBoundingBox().getCenter().subtract(i.position()).normalize().multiply(mult, mult, mult);
                if (add.length() >= 0.1) {
                    if (getBoundingBox().getCenter().distanceTo(i.position()) <= entityData.get(SIZE)/4) {
                        i.setDeltaMovement(add);
                    } else {
                        Vec3 movement = i.getDeltaMovement().add(add);
                        i.setDeltaMovement(movement);
                    }
                }
            }
        }
        for (Entity i : level().getEntitiesOfClass(Entity.class, AABB.ofSize(getBoundingBox().getCenter(), entityData.get(SIZE), entityData.get(SIZE), entityData.get(SIZE)))) {
            i.hurt(damageSources().source(DataNEssence.blackHole), 10);
        }
        entityData.set(SIZE, entityData.get(SIZE) - (1f/20f));
        if (entityData.get(SIZE) <= 0) {
            remove(RemovalReason.KILLED);
        }
    }
}
