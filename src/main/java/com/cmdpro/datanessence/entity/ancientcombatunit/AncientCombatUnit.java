package com.cmdpro.datanessence.entity.ancientcombatunit;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.databank.model.animation.DatabankEntityAnimationState;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.CollisionTestCube;
import com.cmdpro.datanessence.entity.BlackHole;
import com.cmdpro.datanessence.entity.ShockwaveEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@EventBusSubscriber(modid = DataNEssence.MOD_ID)
public class AncientCombatUnit extends Monster {
    @SubscribeEvent
    public static void onShieldBlock(LivingShieldBlockEvent event) {
        if (event.getBlocked()) {
            if (event.getDamageSource().getEntity() instanceof AncientCombatUnit) {
                ItemStack stack = event.getEntity().getUseItem();
                if (event.getEntity().isUsingItem() && stack.getMaxDamage() > 0) {
                    float damage = stack.getMaxDamage()/3f;
                    event.setShieldDamage(damage);
                } else {
                    event.setBlocked(false);
                }
            }
        }
    }

    public DatabankAnimationState animState = new DatabankEntityAnimationState("idle", this).addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("walk", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("spin", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")))
            .addAnim(new DatabankAnimationReference("slashes", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")))
            .addAnim(new DatabankAnimationReference("heavy_slash", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")))
            .addAnim(new DatabankAnimationReference("slam_loop", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("slam_pre", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("slam", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")))
            .addAnim(new DatabankAnimationReference("stun", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")))
            .addAnim(new DatabankAnimationReference("blink", (state, anim) -> {}, (state, anim) -> state.setAnim("idle")));
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS
    );
    public AncientCombatUnit(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        animState.setLevel(level);
        stunHealth = getMaxHealth()/5f;
    }

    private int stunTicks;
    public void stun() {
        stunHealth = getMaxHealth()/5f;
        stunTicks = 50;
        playAnim("stun", true);
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
        return this.level().players().stream().filter((i) -> !i.isDeadOrDying() && i.distanceTo(this) <= 128).toList();
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
        if (stunTicks > 0) {
            setDeltaMovement(0, 0, 0);
        }
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
        lookAtTarget(getTarget());
    }

    public void lookAtTarget(Entity targetEntity) {
        if (targetEntity != null) {
            Vec3 target = targetEntity.position().multiply(1, 0, 1).add(0, getEyePosition().y, 0);
            lookAt(EntityAnchorArgument.Anchor.EYES, target);
            getLookControl().setLookAt(target);
            setYHeadRot(getYRot());

            entityData.set(INSTANT_X_ROTATION, getXRot(), true);
            entityData.set(INSTANT_Y_ROTATION, getYRot(), true);

            hasImpulse = true;
        }
    }

    private int blinks;
    private List<Integer> slashDelays = List.of();
    private String slashType = "";
    private float slashDamage;
    private List<Entity> attackExclusions = new ArrayList<>();
    private int slams = 0;
    private int slamAirTime = 0;
    private boolean slamWasOnGround;
    private float stunHealth;

    public void playAnim(String string, boolean force) {
        animState.setAnim(string);
        entityData.set(ANIMATION, string, force);
    }

    private void startSlam() {
        Optional<? extends Player> target = getFightPlayers().stream().sorted(Comparator.comparing((i) -> i.distanceTo(this))).findFirst();
        if (target.isPresent()) {
            Player targetValue = target.get();
            setTarget(targetValue);
            playSound(SoundEvents.PLAYER_TELEPORT);
            Vec3 pos = targetValue.position();
            pos = pos.add(0, 6, 0);
            teleportTo(pos.x, pos.y, pos.z);
            lookAtTarget(targetValue);
        }
        slamAirTime = 20;
        slamWasOnGround = false;
    }

    @Override
    public boolean isNoGravity() {
        return super.isNoGravity() || slamAirTime > 0;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL)) {
            return false;
        }
        float finalAmount = Math.clamp(amount, 0f, 15f);
        if (stunTicks <= 0) {
            stunHealth -= finalAmount;
            if (stunHealth <= 0) {
                stun();
            }
        }
        return super.hurt(source, finalAmount);
    }


    @Override
    protected boolean canRide(Entity vehicle) {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        this.bossEvent.setProgress(getHealth()/getMaxHealth());

        if (stunTicks <= 0) {
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
                attacks.add("slam");
                if (getPhase() >= 2) {
                    attacks.add("blink_behind");
                }
                int attack = random.nextIntBetweenInclusive(0, attacks.size() - 1);
                String attackId = attacks.get(attack);
                if (attackId.equals("slashes")) {
                    attackCooldown = DEFAULT_COOLDOWN + 35;
                    blinks = 3;
                    teleportToPlayerMaxDist = 6;
                    teleportToPlayer = true;
                    teleportToPlayerDist = 2;
                }
                if (attackId.equals("spin")) {
                    attackCooldown = DEFAULT_COOLDOWN + 30;
                    anim = "spin";
                    force = true;
                    teleportToPlayer = true;
                    teleportToPlayerDist = 2;
                }
                if (attackId.equals("blink_behind")) {
                    attackCooldown = DEFAULT_COOLDOWN + (20 * 3);
                    Optional<? extends Player> target = getFightPlayers().stream().sorted(Comparator.comparing((i) -> i.distanceTo(this))).findFirst();
                    if (target.isPresent()) {
                        Player targetValue = target.get();
                        setTarget(targetValue);
                    }
                }
                if (attackId.equals("slam")) {
                    startSlam();
                    attackCooldown = DEFAULT_COOLDOWN + 30;
                    slams = 2;
                    if (getPhase() >= 2) {
                        slams = 4;
                    }
                    anim = "slam_pre";
                    force = true;
                    slams--;
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
                            teleportTo(pos.x, pos.y, pos.z);
                            lookAtTarget(targetValue);
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
                                pos = pos.add(pos.multiply(1, 0, 1).vectorTo(this.position().multiply(1, 0, 1)).normalize().scale(2).reverse());
                                teleportTo(pos.x, pos.y, pos.z);
                                lookAtTarget();
                                playSound(SoundEvents.PLAYER_TELEPORT);
                                slashDelays = List.of(12);
                                slashDamage = 15;
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
                                slashDamage = 10;
                                slashType = "normal";
                            } else {
                                lookAtTarget();
                                anim = "heavy_slash";
                                force = true;
                                slashDelays = List.of(12);
                                slashDamage = 20;
                                slashType = "heavy";
                            }
                        } else if (blinks > 0) {
                            float distance = Math.min(getTarget().distanceTo(this) - 1.5f, 10f);
                            Vec3 pos = position();
                            pos = pos.add(pos.multiply(1, 0, 1).vectorTo(getTarget().position().multiply(1, 0, 1)).normalize().scale(distance));
                            teleportTo(pos.x, pos.y, pos.z);
                            lookAtTarget();
                            playSound(SoundEvents.PLAYER_TELEPORT);
                            blinks--;
                            attackCooldown = DEFAULT_COOLDOWN + 45;
                            anim = "blink";
                            force = true;
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
                                        if (livingEntity.hurt(damageSources().mobAttack(this), slashDamage)) {
                                            attackExclusions.add(j);
                                        }
                                        if (livingEntity instanceof Player player) {
                                            livingEntity.hurtMarked = true;
                                        }
                                    }
                                    livingEntity.setDeltaMovement(livingEntity.position().multiply(1, 0, 1).vectorTo(hit.getCenter().multiply(1, 0, 1)).normalize().reverse().scale(4).add(0, 1, 0));
                                }
                            }
                        });
                    }
                }
                if (attack.equals("slam")) {
                    if (slamAirTime <= 0) {
                        if (onGround()) {
                            if (!slamWasOnGround) {
                                anim = "slam";
                                force = true;
                                slamWasOnGround = true;
                                playSound(SoundEvents.GENERIC_EXPLODE.value());
                                ((ServerLevel) level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, position().x, position().y, position().z, 1, 0, 0, 0, 0);
                                AABB hit = AABB.ofSize(position(), 5, 6, 5);
                                level().getEntitiesOfClass(Entity.class, hit).forEach((j) -> {
                                    if (j != this) {
                                        if (j instanceof LivingEntity livingEntity) {
                                            j.invulnerableTime = 0;
                                            livingEntity.hurt(damageSources().mobAttack(this), 20);
                                        }
                                    }
                                });
                                ShockwaveEntity shockwave = new ShockwaveEntity(this, level(), 15f, getPhase() >= 2 ? ((1f/20f)*2.5f) : ((1f/20f)*5), 10, 1f, ShockwaveEntity.LIVING_GROUNDED);
                                shockwave.setPos(position().add(0, 0.1, 0));
                                shockwave.ignoreInvincibility = true;
                                level().addFreshEntity(shockwave);
                            }
                            if (slams > 0) {
                                if (attackCooldown - DEFAULT_COOLDOWN <= 25) {
                                    anim = "slam_pre";
                                    force = true;
                                    startSlam();
                                    attackCooldown = DEFAULT_COOLDOWN + 30;
                                    slams--;
                                }
                            }
                        } else {
                            if (!slamWasOnGround) {
                                attackCooldown = DEFAULT_COOLDOWN + 30;
                                anim = "slam_loop";
                                setDeltaMovement(0, -1.5, 0);
                            }
                        }
                    } else {
                        slamWasOnGround = false;
                        attackCooldown = DEFAULT_COOLDOWN + 30;
                        slamAirTime--;
                        anim = "slam_pre";
                    }
                }
            }

            List<Integer> slashDelaysNew = new ArrayList<>();
            for (int i : slashDelays) {
                if (i - 1 <= 0) {
                    if (!attack.equals("blink_behind")) {
                        lookAtTarget();
                    }
                    playSound(SoundEvents.WITCH_THROW);
                    float rot = (float)java.lang.Math.toRadians((-getYRot()-90)+45);
                    CollisionTestCube hit = new CollisionTestCube(AABB.ofSize(getBoundingBox().getCenter().add(new Vec3(1, 0, 1).yRot(rot).scale(1.5f)), 3f, 6, 3f));
                    hit.rotation.rotateY((float)Math.toRadians(getYRot()));
                    List<LivingEntity> hitEntities = hit.getEntitiesOfClass(LivingEntity.class, level(), true);
                    hitEntities.forEach((j) -> {
                        if (j != this) {
                            j.invulnerableTime = 0;
                            j.hurt(damageSources().mobAttack(this), slashDamage);
                        }
                    });
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
                    slashDelaysNew.add(i - 1);
                }
            }
            slashDelays = slashDelaysNew;
            if (!anim.equals("idle") && !anim.equals("walk")) {
                if (!animState.isCurrentAnim(anim) || force) {
                    playAnim(anim, force);
                }
            }
        } else {
            stunTicks--;
            attackCooldown = DEFAULT_COOLDOWN;
            slashDelays = List.of();
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
