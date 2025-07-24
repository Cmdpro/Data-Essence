package com.cmdpro.datanessence.entity;

import com.cmdpro.databank.misc.ColorGradient;
import com.cmdpro.databank.misc.TrailLeftoverHandler;
import com.cmdpro.databank.misc.TrailRender;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.DamageTypeRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EssenceSlashProjectile extends Projectile {
    public int time;
    public EssenceSlashProjectile(EntityType<EssenceSlashProjectile> entityType, Level world) {
        super(entityType, world);
        rotation = random.nextIntBetweenInclusive(-20, 20);
    }
    protected EssenceSlashProjectile(EntityType<EssenceSlashProjectile> entityType, double x, double y, double z, Level world) {
        this(entityType, world);
        this.setPos(x, y, z);
    }
    public EssenceSlashProjectile(EntityType<EssenceSlashProjectile> entityType, LivingEntity shooter, Level world) {
        this(entityType, shooter.getX(), shooter.getEyeY()-0.5f, shooter.getZ(), world);
        this.setOwner(shooter);
        this.setDeltaMovement(shooter.getLookAngle().multiply(0.5, 0.5, 0.5));
    }
    public int rotation;
    @Override
    protected void onHitBlock(BlockHitResult hit) {
        remove(RemovalReason.KILLED);
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
    @Override
    public void tick() {
        previousPos = position();
        this.setPos(this.getX() + getDeltaMovement().x, this.getY() + getDeltaMovement().y, this.getZ() + getDeltaMovement().z);
        if (!level().isClientSide) {
            time++;
            if (time >= 100) {
                remove(RemovalReason.KILLED);
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
    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientRemoval() {
        super.onClientRemoval();
        if (trails != null) {
            for (TrailRender i : trails) {
                TrailLeftoverHandler.addTrail(i, RenderHandler.createBufferSource(), LightTexture.FULL_BRIGHT, getGradient());
            }
        }
    }

    private List<TrailRender> trails;
    public ColorGradient getGradient() {
        return new ColorGradient(
                new Color(143, 0, 215),
                new Color(255, 255, 255)
        ).fadeAlpha(1, 0).fadeAlpha(0, 0, 1, 0.05f);
    }
    public TrailRender getTrail(int index) {
        return getTrails().get(index);
    }
    public List<TrailRender> getTrails() {
        if (trails == null) {
            trails = new ArrayList<>();
            trails.add(new TrailRender(getTrailOffset(0), 20, 20, 0.05f, DataNEssence.locate("textures/vfx/trail.png"),
                    RenderTypeHandler::transparent
            ).setShrink(true).startTicking());
            trails.add(new TrailRender(getTrailOffset(1), 20, 20, 0.05f, DataNEssence.locate("textures/vfx/trail.png"),
                    RenderTypeHandler::transparent
            ).setShrink(true).startTicking());
        }
        return trails;
    }
    public Vec3 getTrailOffset(int index) {
        Vec2 rot = calculateRotationVector(new Vec3(0, 0, 0), getDeltaMovement());
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateY((-rot.y-90) * Mth.DEG_TO_RAD);
        quaternionf.rotateZ(-rot.x * Mth.DEG_TO_RAD);
        quaternionf.rotateX(rotation * Mth.DEG_TO_RAD);
        Vec3 pos = new Vec3(0.2, 0, 0);
        float dist = 0.3f;
        float speed = 16;
        float timeRot = ((level().getGameTime()%(360f*speed))*speed);
        timeRot %= 360;
        timeRot += 180;
        if (index % 2 == 0) {
            dist *= -1;
        }
        float yShift = (float)Math.sin(Math.toRadians(timeRot));
        pos = pos.add((1f-Math.abs(yShift))*0.4f, yShift*dist, 0);
        Vector3f posVec = pos.toVector3f().rotate(quaternionf);
        return new Vec3(posVec.x, posVec.y, posVec.z);
    }
    private Vec2 calculateRotationVector(Vec3 pVec, Vec3 pTarget) {
        double d0 = pTarget.x - pVec.x;
        double d1 = pTarget.y - pVec.y;
        double d2 = pTarget.z - pVec.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return new Vec2(
                Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)))),
                Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F)
        );
    }
}
