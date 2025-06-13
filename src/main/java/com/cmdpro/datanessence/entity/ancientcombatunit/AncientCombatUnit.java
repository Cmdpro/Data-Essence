package com.cmdpro.datanessence.entity.ancientcombatunit;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.databank.model.animation.DatabankEntityAnimationState;
import com.cmdpro.datanessence.entity.BlackHole;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerEntity;
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
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class AncientCombatUnit extends Monster {
    public DatabankAnimationState animState = new DatabankEntityAnimationState("idle", this).addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("walk", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("spin", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")))
            .addAnim(new DatabankAnimationReference("slashes", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")))
            .addAnim(new DatabankAnimationReference("heavy_slash", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")));
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS
    );
    public AncientCombatUnit(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        animState.setLevel(level);
    }

    @Override
    protected void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(AncientCombatUnit.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Float> INSTANT_X_ROTATION = SynchedEntityData.defineId(AncientCombatUnit.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> INSTANT_Y_ROTATION = SynchedEntityData.defineId(AncientCombatUnit.class, EntityDataSerializers.FLOAT);
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(ANIMATION, "idle");
        pBuilder.define(INSTANT_X_ROTATION, 0f);
        pBuilder.define(INSTANT_Y_ROTATION, 0f);
    }
    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
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
        if (INSTANT_X_ROTATION.equals(key)) {
            float rot = entityData.get(INSTANT_X_ROTATION);
            setXRot(rot);
            lerpXRot = rot;
        }
        if (INSTANT_Y_ROTATION.equals(key)) {
            float rot = entityData.get(INSTANT_Y_ROTATION);
            setYRot(rot);
            setYBodyRot(rot);
            setYHeadRot(rot);
            lerpYRot = rot;
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
    public static final int DEFAULT_COOLDOWN = 25;
    public int attackCooldown = DEFAULT_COOLDOWN;
    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (animState.isCurrentAnim("spin")) {
                if (animState.getProgress() >= 0.75 && animState.getProgress() <= 1) {
                    float angle = (float) Math.toRadians((float) ((level().getGameTime() % 20)) * 45f);
                    for (int i = 0; i < 5; i++) {
                        float currentAngle = angle + (float) Math.toRadians(15f * i);
                        float scale = 2f;
                        Vec3 pos = getBoundingBox().getCenter().add(Math.sin(currentAngle) * scale, 0, Math.cos(currentAngle) * scale);
                        level().addParticle(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 0, 0, 0);
                        pos = getBoundingBox().getCenter().add(Math.sin(currentAngle) * -scale, 0, Math.cos(currentAngle) * -scale);
                        level().addParticle(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 0, 0, 0);
                    }
                }
            }
        }
    }

    public void lookAtTarget() {
        Vec3 target = getTarget().position().multiply(1, 0, 1).add(0, getEyePosition().y, 0);
        lookAt(EntityAnchorArgument.Anchor.EYES, target);
        getLookControl().setLookAt(target);
        setYHeadRot(getYRot());

        entityData.set(INSTANT_X_ROTATION, getXRot(), true);
        entityData.set(INSTANT_Y_ROTATION, getYRot(), true);

        hasImpulse = true;
    }

    private int blinks;
    private List<Integer> slashDelays = List.of();
    private String slashType = "";
    private float slashDamage;
    private List<Entity> attackExclusions = new ArrayList<>();

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
            attackCooldown = DEFAULT_COOLDOWN;
            attackExclusions = new ArrayList<>();
            boolean teleportToPlayer = false;
            float teleportToPlayerDist = 0;
            float teleportToPlayerMaxDist = Float.MAX_VALUE;
            List<String> attacks = new ArrayList<>();
            attacks.add("slashes");
            attacks.add("spin");
            if (getPhase() >= 2) {
                attacks.add("blink_behind");
            }
            int attack = random.nextIntBetweenInclusive(0, attacks.size()-1);
            String attackId = attacks.get(attack);
            if (attackId.equals("slashes")) {
                attackCooldown = DEFAULT_COOLDOWN+35;
                blinks = 3;
                teleportToPlayerMaxDist = 6;
                teleportToPlayer = true;
                teleportToPlayerDist = 2;
            }
            if (attackId.equals("spin")) {
                attackCooldown = DEFAULT_COOLDOWN+30;
                anim = "spin";
                force = true;
                teleportToPlayer = true;
                teleportToPlayerDist = 2;
            }
            if (attackId.equals("blink_behind")) {
                attackCooldown = DEFAULT_COOLDOWN+(20*3);
            }
            this.attack = attackId;

            if (teleportToPlayer) {
                Optional<? extends Player> target = getFightPlayers().stream().sorted(Comparator.comparing((i) -> i.distanceTo(this))).findFirst();
                if (target.isPresent()) {
                    Player targetValue = target.get();
                    setTarget(targetValue);
                    if (targetValue.distanceTo(this) <= teleportToPlayerMaxDist) {
                        playSound(SoundEvents.PLAYER_TELEPORT);
                        Vec3 pos = targetValue.position();
                        Vec3 diff = pos.multiply(1, 0, 1).vectorTo(this.position().multiply(1, 0, 1));
                        pos = pos.add(diff.normalize().scale(teleportToPlayerDist));
                        lookAtTarget();
                        teleportTo(pos.x, pos.y, pos.z);
                    }
                }
            }
        }

        if (attack != null) {
            if (attack.equals("blink_behind")) {
                if (attackCooldown > DEFAULT_COOLDOWN) {
                    if ((attackCooldown - DEFAULT_COOLDOWN) % 20 == 19) {
                        if (getTarget() != null) {
                            Vec3 pos = getTarget().position();
                            pos = pos.add(pos.multiply(1, 0, 1).vectorTo(this.position().multiply(1, 0, 1)).normalize().reverse());
                            lookAtTarget();
                            teleportTo(pos.x, pos.y, pos.z);
                            playSound(SoundEvents.PLAYER_TELEPORT);
                            slashDelays = List.of(12);
                            slashDamage = 10;
                            slashType = "heavy";
                            anim = "heavy_slash";
                            force = true;
                        }
                    }
                }
            }

            if (attack.equals("slashes") && attackCooldown == DEFAULT_COOLDOWN + 35) {
                if (getTarget() != null) {
                    if (getTarget().distanceTo(this) <= 6) {
                        if (blinks >= 3) {
                            lookAtTarget();
                            blinks = 0;
                            anim = "slashes";
                            force = true;
                            slashDelays = List.of(13, 18, 25);
                            slashDamage = 5;
                            slashType = "normal";
                        } else {
                            lookAtTarget();
                            anim = "heavy_slash";
                            force = true;
                            slashDelays = List.of(12);
                            slashDamage = 10;
                            slashType = "heavy";
                        }
                    } else if (blinks > 0) {
                        float distance = Math.min(getTarget().distanceTo(this) - 1.5f, 10f);
                        Vec3 pos = position();
                        pos = pos.add(pos.multiply(1, 0, 1).vectorTo(getTarget().position().multiply(1, 0, 1)).normalize().scale(distance));
                        lookAtTarget();
                        teleportTo(pos.x, pos.y, pos.z);
                        playSound(SoundEvents.PLAYER_TELEPORT);
                        blinks--;
                        attackCooldown = DEFAULT_COOLDOWN + 45;
                    }
                }
            }
            if (attack.equals("spin")) {
                if (attackCooldown >= DEFAULT_COOLDOWN + 10 && attackCooldown <= DEFAULT_COOLDOWN + 15) {
                    playSound(SoundEvents.WITCH_THROW);
                    AABB hit = AABB.ofSize(getBoundingBox().getCenter(), 6, 6, 6);
                    level().getEntitiesOfClass(Entity.class, hit).forEach((j) -> {
                        if (j != this) {
                            if (j instanceof Projectile projectile) {
                                projectile.setDeltaMovement(projectile.position().vectorTo(hit.getCenter()).normalize().reverse().scale(projectile.getDeltaMovement().length()));
                            } else if (j instanceof LivingEntity livingEntity) {
                                if (!attackExclusions.contains(j)) {
                                    j.invulnerableTime = 0;
                                    livingEntity.hurt(damageSources().mobAttack(this), slashDamage);
                                    if (livingEntity instanceof Player) {
                                        livingEntity.hurtMarked = true;
                                    }
                                    attackExclusions.add(j);
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
                AABB hit = AABB.ofSize(getBoundingBox().getCenter().add(getLookAngle().scale(1.5f)), 5, 6, 5);
                level().getEntitiesOfClass(LivingEntity.class, hit).forEach((j) -> {
                    if (j != this) {
                        j.invulnerableTime = 0;
                        j.hurt(damageSources().mobAttack(this), slashDamage);
                    }
                });
                lookAtTarget();
                if (slashType.equals("normal")) {
                    if (getTarget() != null) {
                        Vec3 pos = position().multiply(1, 0, 1).vectorTo(getTarget().position().multiply(1, 0, 1)).normalize().scale(0.75f);
                        teleportRelative(pos.x, pos.y, pos.z);
                    }
                } else {
                    if (getTarget() != null) {
                        Vec3 pos = position().multiply(1, 0, 1).vectorTo(getTarget().position().multiply(1, 0, 1)).normalize().scale(1.5f);
                        teleportRelative(pos.x, pos.y, pos.z);
                    }
                }
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
