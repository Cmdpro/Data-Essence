package com.cmdpro.datanessence.entity.ancientcombatunit;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.entity.BlackHole;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class AncientCombatUnit extends Monster {
    public DatabankAnimationState animState = new DatabankAnimationState("idle")
            .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("walk", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("spin", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")))
            .addAnim(new DatabankAnimationReference("slashes", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")))
            .addAnim(new DatabankAnimationReference("heavy_slash", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")));
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS
    );
    public AncientCombatUnit(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(AncientCombatUnit.class, EntityDataSerializers.STRING);
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(ANIMATION, "idle");
    }
    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 250.0D)
                .add(Attributes.ATTACK_DAMAGE, 0f)
                .add(Attributes.ATTACK_SPEED, 0f)
                .add(Attributes.MOVEMENT_SPEED, 0f).build();
    }
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new AncientCombatUnitMoveGoal(this, 1.0D, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }


    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        playSound(SoundEvents.IRON_GOLEM_STEP);
    }

    protected SoundEvent getAmbientSound() {
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }
    protected float getSoundVolume() {
        return 0.2F;
    }

    @Override
    public void checkDespawn() {
        if (getFightPlayers().isEmpty()) {
            this.discard();
        }
    }
    public List<? extends Player> getFightPlayers() {
        return this.level().players().stream().filter((i) -> i.distanceTo(this) <= 128).toList();
    }
    public boolean canMove() {
        return attackCooldown <= 50;
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ANIMATION.equals(key)) {
            animState.setAnim(entityData.get(ANIMATION));
            animState.resetAnim();
        }

        super.onSyncedDataUpdated(key);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
    public int getPhase() {
        if (getHealth() <= getMaxHealth()/2) {
            return 2;
        }
        return 1;
    }
    public String attack;
    public int attackCooldown = 50;
    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {

        }
    }

    private int blinks;
    private List<Integer> slashDelays = List.of();
    private float slashDamage;

    public void playAnim(String string, boolean force) {
        animState.setAnim(string);
        entityData.set(ANIMATION, string, force);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        this.bossEvent.setProgress(getHealth()/getMaxHealth());

        String anim = animState.getAnim() == null ? animState.defaultAnim : animState.getAnim().id;
        boolean force = false;

        attackCooldown--;
        if (attackCooldown <= 0) {
            attackCooldown = 50;
            List<String> attacks = new ArrayList<>();
            attacks.add("slashes");
            attacks.add("spin");
            if (getPhase() >= 2) {
                attacks.add("blink_behind");
            }
            int attack = random.nextIntBetweenInclusive(0, attacks.size()-1);
            String attackId = attacks.get(attack);
            if (attackId.equals("slashes")) {
                attackCooldown = 50+35;
                blinks = 3;
                Optional<? extends Player> target = getFightPlayers().stream().sorted(Comparator.comparing((i) -> i.distanceTo(this))).findFirst();
                target.ifPresent(this::setTarget);
                if (getTarget() != null) {
                    if (getTarget().distanceTo(this) <= 6) {
                        playSound(SoundEvents.PLAYER_TELEPORT);
                        Vec3 pos = getTarget().position();
                        pos = pos.add(pos.multiply(1, 0, 1).vectorTo(this.position().multiply(1, 0, 1)).normalize());
                        teleportTo(pos.x, pos.y, pos.z);
                    }
                }
            }
            if (attackId.equals("spin")) {
                attackCooldown = 50+30;
                anim = "spin";
                force = true;
            }
            if (attackId.equals("blink_behind")) {
                attackCooldown = 50+(20*3);
            }
            this.attack = attackId;
        }

        if (attack != null) {
            if (attack.equals("blink_behind")) {
                if (attackCooldown > 50) {
                    if ((attackCooldown - 50) % 20 == 19) {
                        if (getTarget() != null) {
                            Vec3 pos = getTarget().position();
                            pos = pos.add(pos.multiply(1, 0, 1).vectorTo(this.position().multiply(1, 0, 1)).normalize().reverse());
                            teleportTo(pos.x, pos.y, pos.z);
                            lookAt(EntityAnchorArgument.Anchor.EYES, getTarget().getEyePosition());
                            playSound(SoundEvents.PLAYER_TELEPORT);
                            slashDelays = List.of(12);
                            slashDamage = 10;
                            anim = "heavy_slash";
                            force = true;
                        }
                    }
                }
            }

            if (attack.equals("slashes") && attackCooldown == 50 + 35) {
                if (getTarget() != null) {
                    if (getTarget().distanceTo(this) <= 6) {
                        lookAt(EntityAnchorArgument.Anchor.EYES, getTarget().getEyePosition());
                        if (blinks >= 3) {
                            blinks = 0;
                            anim = "slashes";
                            force = true;
                            slashDelays = List.of(10, 15, 21);
                            slashDamage = 5;
                        } else {
                            anim = "heavy_slash";
                            force = true;
                            slashDelays = List.of(12);
                            slashDamage = 10;
                        }
                    } else if (blinks > 0) {
                        float distance = Math.min(getTarget().distanceTo(this) - 1.5f, 5f);
                        Vec3 pos = position();
                        pos = pos.multiply(1, 0, 1).vectorTo(getTarget().position().multiply(1, 0, 1)).normalize().scale(distance);
                        teleportRelative(pos.x, pos.y, pos.z);
                        lookAt(EntityAnchorArgument.Anchor.EYES, getTarget().getEyePosition());
                        playSound(SoundEvents.PLAYER_TELEPORT);
                        blinks--;
                        attackCooldown = 50 + 45;
                    }
                }
            }
            if (attack.equals("spin")) {
                if (attackCooldown >= 50 + 10 && attackCooldown <= 50 + 15) {
                    AABB hit = AABB.ofSize(getBoundingBox().getCenter(), 5, 5, 5);
                    level().getEntitiesOfClass(Entity.class, hit).forEach((j) -> {
                        if (j != this) {
                            if (j instanceof Projectile projectile) {
                                projectile.setDeltaMovement(projectile.position().vectorTo(hit.getCenter()).normalize().reverse().scale(projectile.getDeltaMovement().length()));
                            } else if (j instanceof LivingEntity livingEntity) {
                                livingEntity.hurt(damageSources().mobAttack(this), slashDamage);
                                if (livingEntity instanceof Player) {
                                    livingEntity.hurtMarked = true;
                                }
                                livingEntity.setDeltaMovement(livingEntity.position().vectorTo(hit.getCenter()).normalize().reverse().scale(3).add(0, 2, 0));
                            }
                        }
                    });
                }
            }
        }

        List<Integer> slashDelaysNew = new ArrayList<>();
        for (int i : slashDelays) {
            if (i - 1 <= 0) {
                playSound(SoundEvents.WITCH_THROW);
                AABB hit = AABB.ofSize(getBoundingBox().getCenter().add(getLookAngle().scale(1f)), 2, 3, 2);
                level().getEntitiesOfClass(LivingEntity.class, hit).forEach((j) -> {
                    if (j != this) {
                        j.hurt(damageSources().mobAttack(this), slashDamage);
                    }
                });
            } else {
                slashDelaysNew.add(i-1);
            }
        }
        slashDelays = slashDelaysNew;
        if (!anim.equals("idle") && !anim.equals("walk")) {
            if (!animState.isCurrentAnim(anim) || force) {
                playAnim(anim, force);
            }
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }
    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }
}
