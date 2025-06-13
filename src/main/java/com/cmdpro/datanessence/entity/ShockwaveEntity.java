package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.registry.DamageTypeRegistry;
import com.cmdpro.datanessence.registry.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

public class ShockwaveEntity extends Entity implements TraceableEntity {
    public static final Selector PLAYERS = (entity) -> entity instanceof Player;
    public static final Selector NON_PLAYERS = (entity) -> !(entity instanceof Player);
    public static final Selector LIVING = (entity) -> entity instanceof LivingEntity;
    public static final Selector ALL = (entity) -> true;
    public static final Selector ALL_GROUNDED = Entity::onGround;
    public static final Selector PLAYERS_GROUNDED = (entity) -> entity instanceof Player && entity.onGround();
    public static final Selector NON_PLAYERS_GROUNDED = (entity) -> !(entity instanceof Player) && entity.onGround();
    public static final Selector LIVING_GROUNDED = (entity) -> entity instanceof LivingEntity && entity.onGround();
    public float size;
    public float maxSize;
    public float speed;
    public boolean ignoreInvincibility;
    public Selector selector;
    public ShockwaveEntity(EntityType<?> type, Level level) {
        super(type, level);
    }
    public ShockwaveEntity(LivingEntity owner, Level pLevel, float maxSize, float speed, float damage, float launchVelocity) {
        this(owner, pLevel, maxSize, speed, damage, launchVelocity, ALL);
    }
    public ShockwaveEntity(LivingEntity owner, Level pLevel, float maxSize, float speed, float damage, float launchVelocity, Selector selector) {
        this(owner, pLevel, maxSize, speed, damage, launchVelocity, selector, null);
    }
    public ShockwaveEntity(LivingEntity owner, Level pLevel, float maxSize, float speed, float damage, float launchVelocity, Selector selector, DamageSource source) {
        super(EntityRegistry.SHOCKWAVE.get(), pLevel);
        this.owner = owner;
        this.maxSize = maxSize;
        this.speed = speed;
        this.damage = damage;
        this.damageSource = source == null ? damageSources().mobProjectile(this, owner) : source;
        this.launchVelocity = launchVelocity;
        this.selector = selector;
    }

    public float damage;
    public float launchVelocity;
    public DamageSource damageSource;

    public static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(ShockwaveEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> MAX_SIZE = SynchedEntityData.defineId(ShockwaveEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SIZE_SPEED = SynchedEntityData.defineId(ShockwaveEntity.class, EntityDataSerializers.FLOAT);
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(SIZE, 0f);
        pBuilder.define(MAX_SIZE, 0f);
        pBuilder.define(SIZE_SPEED, 0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    private List<Entity> attackExclusions = new ArrayList<>();
    private LivingEntity owner;
    @Override
    public @Nullable Entity getOwner() {
        return owner;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            maxSize = getEntityData().get(MAX_SIZE);
            size = getEntityData().get(SIZE);
            speed = getEntityData().get(SIZE_SPEED);
            size += speed;
        } else {
            for (Entity i : level().getEntitiesOfClass(Entity.class, AABB.ofSize(position().add(0, 0.5, 0), size*2, 4, size*2))) {
                if (i.equals(owner)) {
                    continue;
                }
                if (!selector.isValid(i)) {
                    continue;
                }
                if (attackExclusions.contains(i)) {
                    continue;
                }
                Vec3 vec = i.position().multiply(1, 0, 1).vectorTo(position().multiply(1, 0, 1)).normalize();
                Vec3 closest = i.position().add(vec.scale(0.4));
                Vec3 furthest = i.position().add(vec.scale(-0.4));
                if (closest.multiply(1, 0, 1).distanceTo(position().multiply(1, 0, 1)) <= size
                        && furthest.multiply(1, 0, 1).distanceTo(position().multiply(1, 0, 1)) >= size) {
                    if (ignoreInvincibility) {
                        i.invulnerableTime = 0;
                    }
                    if (i.hurt(damageSource, damage)) {
                        if (ignoreInvincibility) {
                            attackExclusions.add(i);
                        }
                    }
                    if (i instanceof Player) {
                        i.hurtMarked = true;
                    }
                    i.setDeltaMovement(i.getDeltaMovement().x, launchVelocity, i.getDeltaMovement().z);
                }
            }

            size += speed;

            entityData.set(MAX_SIZE, maxSize);
            entityData.set(SIZE, size);
            entityData.set(SIZE_SPEED, speed);

            if (size >= maxSize) {
                discard();
            }
        }
    }
    public interface Selector {
        boolean isValid(Entity entity);
    }
}
