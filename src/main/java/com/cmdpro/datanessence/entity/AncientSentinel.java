package com.cmdpro.datanessence.entity;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.databank.model.animation.DatabankEntityAnimationState;
import com.cmdpro.datanessence.registry.EntityRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumSet;


public class AncientSentinel extends Monster {
    public DatabankAnimationState animState = new DatabankEntityAnimationState("idle", this)
            .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}));
    public AncientSentinel(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        animState.setLevel(level);
    }
    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 0f)
                .add(Attributes.ATTACK_SPEED, 0f)
                .add(Attributes.MOVEMENT_SPEED, 0f).build();
    }

    @Override
    protected void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new AncientSentinel.SentinelAttackGoal(this, 1.0D, 25, 50, 15));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }


    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        playSound(SoundRegistry.ANCIENT_SENTINEL_WALK.value());
    }

    protected SoundEvent getAmbientSound() {
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundRegistry.ANCIENT_SENTINEL_HURT.value();
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.ANCIENT_SENTINEL_DEATH.value();
    }
    protected float getSoundVolume() {
        return 0.2F;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        }
    }

    public void performRangedAttack(LivingEntity pTarget, AttackType type) {
        if (type == AttackType.LASER) {
            laserTarget = pTarget.getEyePosition();
            laserTime = 20;
            getEntityData().set(LASER_TIME, laserTime, true);
            getEntityData().set(LASER_TARGET, laserTarget.toVector3f(), true);
        } else if (type == AttackType.SHOOT) {
            AncientSentinelProjectile projectile = new AncientSentinelProjectile(EntityRegistry.ANCIENT_SENTINEL_PROJECTILE.get(), this, level());
            double d0 = pTarget.getX() - this.getX();
            double d1 = (pTarget.getEyeY() - 0.1F) - projectile.getY();
            double d2 = pTarget.getZ() - this.getZ();
            Vec3 vec = new Vec3(d0, d1, d2).normalize();
            projectile.setDeltaMovement(vec.multiply(0.5f, 0.5f, 0.5f));
            this.playSound(SoundRegistry.ANCIENT_SENTINEL_SHOOT.value(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level().addFreshEntity(projectile);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LASER_TIME, -1);
        builder.define(LASER_TARGET, new Vector3f());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == LASER_TIME) {
            maxLaserTime = getEntityData().get(LASER_TIME);
            laserTime = maxLaserTime;
        }
        if (key == LASER_TARGET) {
            Vector3f vec = getEntityData().get(LASER_TARGET);
            laserTarget = new Vec3(vec.x, vec.y, vec.z);
        }
    }

    public static final EntityDataAccessor<Integer> LASER_TIME = SynchedEntityData.defineId(AncientSentinel.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Vector3f> LASER_TARGET = SynchedEntityData.defineId(AncientSentinel.class, EntityDataSerializers.VECTOR3);
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (laserTarget == null) {
            laserTime = -1;
        }
        if (laserTime >= 0) {
            laserTime--;
            lookAt(EntityAnchorArgument.Anchor.EYES, laserTarget);
            if (laserTime == 0) {
                AncientSentinelLaser laser = new AncientSentinelLaser(EntityRegistry.ANCIENT_SENTINEL_LASER.get(), this, level());
                laser.setDeltaMovement(getEyePosition().vectorTo(laserTarget).normalize());
                level().addFreshEntity(laser);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (laserTime >= 0) {
                laserTime--;
                lookAt(EntityAnchorArgument.Anchor.EYES, laserTarget);
            }
        }
    }

    public boolean canProgressInterval() {
        return laserTime < 0;
    }
    public int laserTime = -1;
    public int maxLaserTime = -1;
    public Vec3 laserTarget;
    protected static class SentinelAttackGoal extends Goal {
        private final Mob mob;
        private final AncientSentinel ancientSentinel;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int shootAttackInterval;
        private final int laserAttackInterval;
        private final float attackRadius;
        private AttackType currentType = AttackType.NONE;
        private static final float MAX_SHOOT_DISTANCE = 8f;

        public SentinelAttackGoal(AncientSentinel ancientSentinel, double speedModifier, int shootAttackInterval, int laserAttackInterval, float attackRadius) {
            if (ancientSentinel == null) {
                throw new IllegalArgumentException("SentinelAttackGoal requires Mob implements AncientSentinel");
            } else {
                this.ancientSentinel = ancientSentinel;
                this.mob = (Mob)ancientSentinel;
                this.speedModifier = speedModifier;
                this.shootAttackInterval = shootAttackInterval;
                this.laserAttackInterval = laserAttackInterval;
                this.attackRadius = attackRadius;
                this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            }
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
        }

        @Override
        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.target == null) {
                return;
            }
            double distance = this.mob.position().distanceTo(this.target.position());
            boolean canSee = this.mob.getSensing().hasLineOfSight(this.target);
            AttackType type = currentType;
            if (canSee) {
                this.seeTime++;
                type = AttackType.SHOOT; // laser rendering still WIP
//                if (distance <= MAX_SHOOT_DISTANCE) {
//                    type = AttackType.SHOOT;
//                } else {
//                    type = AttackType.LASER;
//                }
            } else {
                this.seeTime = 0;
                type = AttackType.NONE;
            }
            if (type != currentType) {
                currentType = type;
            }

            if (!(distance > (double)this.attackRadius) && this.seeTime >= 5) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().moveTo(this.target, this.speedModifier);
            }

            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (ancientSentinel.canProgressInterval()) this.attackTime -= 1;
            if (this.attackTime == 0) {
                if (type == AttackType.NONE) {
                    return;
                }
                this.ancientSentinel.performRangedAttack(this.target, currentType);
                this.attackTime = type == AttackType.SHOOT ? shootAttackInterval : laserAttackInterval;
            } else if (this.attackTime < 0) {
                this.attackTime = type == AttackType.SHOOT ? shootAttackInterval : laserAttackInterval;
            }
        }
    }
    public enum AttackType {
        NONE,
        SHOOT,
        LASER
    }
}
