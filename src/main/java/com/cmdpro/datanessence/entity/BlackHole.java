package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.joml.Math;

public class BlackHole extends Entity {
    public BlackHole(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public void tick() {
        super.tick();
        for (Entity i : level().getEntitiesOfClass(Entity.class, AABB.ofSize(getBoundingBox().getCenter(), 20, 20, 20))) {
            if (i != this && !i.getType().is(Tags.EntityTypes.BOSSES)) {
                if (i instanceof Player) {
                    i.hurtMarked = true;
                }
                float mult = Math.clamp(0, 0.5f, 20-i.distanceTo(this));
                Vec3 add = getBoundingBox().getCenter().subtract(i.position()).normalize().multiply(mult, mult, mult);
                if (add.length() >= 0.1) {
                    Vec3 movement = i.getDeltaMovement().add(add);
                    i.setDeltaMovement(movement);
                }
            }
        }
        for (Entity i : level().getEntitiesOfClass(Entity.class, AABB.ofSize(getBoundingBox().getCenter(), 4, 4, 4))) {
            i.hurt(damageSources().source(DataNEssence.blackHole), 10);
        }
    }
}
