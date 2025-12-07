package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.block.TraversiteBlock;
import com.cmdpro.datanessence.block.transportation.TraversiteRoad;
import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import com.cmdpro.datanessence.client.particle.RhombusParticleOptions;
import com.cmdpro.datanessence.client.particle.SmallCircleParticleOptions;
import com.cmdpro.datanessence.registry.DamageTypeRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import javax.annotation.Nullable;
import java.awt.*;

public class AncientSentinelProjectile extends Projectile {

    public int time;
    public AncientSentinelProjectile(EntityType<AncientSentinelProjectile> entityType, Level world) {
        super(entityType, world);
    }
    protected AncientSentinelProjectile(EntityType<AncientSentinelProjectile> entityType, double x, double y, double z, Level world) {
        this(entityType, world);
        this.setPos(x, y, z);
    }
    public AncientSentinelProjectile(EntityType<AncientSentinelProjectile> entityType, LivingEntity shooter, Level world) {
        this(entityType, shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ(), world);
        this.setOwner(shooter);
        this.setDeltaMovement(shooter.getLookAngle().multiply(0.5, 0.5, 0.5));
    }
    @Override
    protected void onHitBlock(BlockHitResult hit) {
        float pitch = Mth.nextFloat(getRandom(), 0.9f, 1.1f);
        if (!hasAimed) {
            reAim();
        } else if (blockInvincibility <= 0) {
            pitch *= 0.75f;
            remove(RemovalReason.KILLED);
        }
        level().playSound(null, blockPosition(), SoundRegistry.ANCIENT_SENTINEL_PROJECTILE_HIT_BLOCK.value(), SoundSource.HOSTILE, 1.0f, pitch);
        super.onHitBlock(hit);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("time", (int)this.time);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.time = tag.getInt("time");
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 p_36758_, Vec3 p_36759_) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, p_36758_, p_36759_, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }
    Vec3 previousPos;

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {

    }
    public int blockInvincibility;
    public void reAim() {
        Entity entity = getOwner();
        if (entity != null) {
            double speed = getDeltaMovement().length()/2f;
            setDeltaMovement(entity.getEyePosition().subtract(getBoundingBox().getCenter()).normalize().scale(speed));
            hasImpulse = true;
            hasAimed = true;
            blockInvincibility = 2;
        }
    }
    boolean hasAimed = false;
    @Override
    public void tick() {
        previousPos = position();
        this.setPos(this.getX() + getDeltaMovement().x, this.getY() + getDeltaMovement().y, this.getZ() + getDeltaMovement().z);
        if (!level().isClientSide) {
            if (blockInvincibility > 0) {
                blockInvincibility--;
            }
            time++;
            if (time >= 100) {
                remove(RemovalReason.KILLED);
            }
            if (time >= 20) {
                if (!hasAimed) {
                    reAim();
                }
            }
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }

            while(!this.isRemoved()) {
                EntityHitResult entityhitresult = this.findHitEntity(this.position(), this.position().add(getDeltaMovement()));
                if (entityhitresult != null) {
                    hitresult = entityhitresult;
                }

                if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult)hitresult).getEntity();
                    Entity entity1 = this.getOwner();
                    if ((entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity))) {
                        hitresult = null;
                        entityhitresult = null;
                    }
                }

                if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult)) {
                    this.onHit(hitresult);
                    this.hasImpulse = true;
                }

                if (entityhitresult == null) {
                    break;
                }

                hitresult = null;
            }

        } else {
            for (double i = 0; i < 3; i++) {
                for (int o = 0; o < 2; o++) {
                    Vec3 pos = position();
                    pos = pos.subtract(getDeltaMovement().x*(i/3d), getDeltaMovement().y*(i/3d), getDeltaMovement().z*(i/3d));
                    pos = pos.add(0, getBoundingBox().getYsize() / 2f, 0);
                    pos = pos.add(Mth.nextFloat(random, -0.1f, 0.1f), Mth.nextFloat(random, -0.1f, 0.1f), Mth.nextFloat(random, -0.1f, 0.1f));
                    ClientMethods.particle(random, pos, level());
                }
            }
        }
        super.tick();
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        Entity entity = this.getOwner();
        if (entity == hit.getEntity()) {
            remove(RemovalReason.KILLED);
            return;
        }
        DamageSource damagesource;
        if (entity instanceof LivingEntity) {
            damagesource = this.damageSources().source(DamageTypeRegistry.ancientProjectile, this, entity);
            ((LivingEntity)entity).setLastHurtMob(hit.getEntity());
        } else if (entity instanceof Player) {
            damagesource = this.damageSources().source(DamageTypeRegistry.ancientProjectile, this, entity);
            ((LivingEntity)entity).setLastHurtMob(hit.getEntity());
        } else {
            damagesource = null;
        }
        boolean flag = hit.getEntity().getType() == EntityType.ENDERMAN;
        if (damagesource != null) {
            if (hit.getEntity() instanceof LivingEntity ent && entity instanceof LivingEntity) {
                boolean isPercent = getOwner() instanceof AncientSentinel sentinel && sentinel.isPercentDamage(this, ent);
                float percent = isPercent ? getOwner() instanceof AncientSentinel sentinel ? sentinel.getPercentDamage(this, ent) : 0 : 0;
                if (ent.hurt(damagesource, isPercent ? ent.getMaxHealth()*percent : 2)) {
                    if (flag) {
                        return;
                    }
                }
            }
        }
        remove(RemovalReason.KILLED);
    }
    public static class ClientMethods {
        public static void particle(RandomSource random, Vec3 pos, Level level) {
            for (int i = 0; i < 3; i++) {
                Vec3 offset = new Vec3(Mth.nextFloat(random, -1f, 1f), Mth.nextFloat(random, -1f, 1f), Mth.nextFloat(random, -1f, 1f)).normalize().multiply(0.1f, 0.1f, 0.1f);
                level.addParticle(new CircleParticleOptions().setColor(Color.getHSBColor((float) (level.getGameTime() % 100) / 100f, 1f, 1f)).setAdditive(true), pos.x + offset.x, pos.y + offset.y, pos.z + offset.z, 0, 0, 0);
            }
        }
    }
}
