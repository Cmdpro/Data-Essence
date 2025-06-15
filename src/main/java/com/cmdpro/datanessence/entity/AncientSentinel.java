package com.cmdpro.datanessence.entity;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.databank.model.animation.DatabankEntityAnimationState;
import com.cmdpro.datanessence.registry.EntityRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;


public class AncientSentinel extends Monster implements RangedAttackMob {
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
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 30, 15));
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

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {
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
