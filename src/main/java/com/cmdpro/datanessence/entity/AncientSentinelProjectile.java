package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.client.particle.CircleParticleOptionsAdditive;
import com.cmdpro.datanessence.registry.DamageTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;
import org.apache.commons.lang3.RandomUtils;

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
        if (!hasAimed) {
            reAim();
        } else if (blockInvincibility <= 0) {
            remove(RemovalReason.KILLED);
        }
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
        Player player = null;
        for (Player i : level().getEntitiesOfClass(Player.class, AABB.ofSize(getBoundingBox().getCenter(), 30, 30, 30))) {
            if (player == null || player.getEyePosition().distanceTo(getBoundingBox().getCenter()) > i.getEyePosition().distanceTo(getBoundingBox().getCenter())) {
                player = i;
            }
        }
        if (player != null) {
            setDeltaMovement(player.getEyePosition().subtract(0, 0.1f, 0).subtract(getBoundingBox().getCenter()).normalize().multiply(0.5, 0.5, 0.5));
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
                    pos = pos.add(RandomUtils.nextFloat(0, 0.2f) - 0.1f, RandomUtils.nextFloat(0, 0.2f) - 0.1f, RandomUtils.nextFloat(0, 0.2f) - 0.1f);
                    ClientMethods.particle(pos, level());
                }
            }
        }
        super.tick();
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        Entity entity = this.getOwner();
        DamageSource damagesource;
        if (entity instanceof LivingEntity) {
            damagesource = this.damageSources().source(DamageTypeRegistry.magicProjectile, this, entity);
            ((LivingEntity)entity).setLastHurtMob(hit.getEntity());
        } else if (entity instanceof Player) {
            damagesource = this.damageSources().source(DamageTypeRegistry.magicProjectile, this, entity);
            ((LivingEntity)entity).setLastHurtMob(hit.getEntity());
        } else {
            damagesource = null;
        }
        boolean flag = hit.getEntity().getType() == EntityType.ENDERMAN;
        if (damagesource != null) {
            if (hit.getEntity() instanceof LivingEntity ent && entity instanceof LivingEntity) {
                if (ent.hurt(damagesource, 5)) {
                    if (flag) {
                        return;
                    }
                }
            }
        }
        remove(RemovalReason.KILLED);
    }
    public static class ClientMethods {
        public static void particle(Vec3 pos, Level level) {
            for (int i = 0; i < 3; i++) {
                Vec3 offset = new Vec3(RandomUtils.nextFloat(0f, 2f) - 1f, RandomUtils.nextFloat(0f, 2f) - 1f, RandomUtils.nextFloat(0f, 2f) - 1f).normalize().multiply(0.1f, 0.1f, 0.1f);
                level.addParticle(new CircleParticleOptionsAdditive(Color.getHSBColor((float) (level.getGameTime() % 100) / 100f, 1f, 1f)), pos.x + offset.x, pos.y + offset.y, pos.z + offset.z, 0, 0, 0);
            }
        }
    }
}
