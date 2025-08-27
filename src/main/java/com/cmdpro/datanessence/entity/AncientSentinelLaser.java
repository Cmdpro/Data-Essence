package com.cmdpro.datanessence.entity;

import com.cmdpro.databank.misc.CollisionTestCube;
import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.awt.*;

public class AncientSentinelLaser extends Entity {
    public LivingEntity owner;
    public int time;
    public final int maxTime = 10;
    public AncientSentinelLaser(EntityType<AncientSentinelLaser> entityType, Level world) {
        super(entityType, world);
    }
    protected AncientSentinelLaser(EntityType<AncientSentinelLaser> entityType, double x, double y, double z, Level world) {
        this(entityType, world);
        this.setPos(x, y, z);
    }
    public AncientSentinelLaser(EntityType<AncientSentinelLaser> entityType, LivingEntity shooter, Level world) {
        this(entityType, shooter.getX(), shooter.getEyeY(), shooter.getZ(), world);
        this.owner = shooter;
        this.setDeltaMovement(shooter.getLookAngle());
    }

    private boolean shot;
    private Vec3 end;
    @Override
    public void tick() {
        super.tick();
        HitResult hit = getHit(20, false);
        end = hit.getLocation();
        time++;
        if (level().isClientSide) {
            if (!shot) {
                ClientMethods.particle(getRandom(), end, level());
                shot = true;
            }
        } else {
            if (!shot) {
                CollisionTestCube cube = new CollisionTestCube(AABB.ofSize(position().lerp(end, 0.5f), 0.5, 0.5, position().distanceTo(end)*2f));
                Quaternionf rotation = new Quaternionf();
                Vec2 angle = calculateRotationVector(position(), end);
                rotation.rotateY((float) Math.toRadians(-angle.y + 180));
                rotation.rotateX((float)Math.toRadians(-angle.x));
                cube.rotation = rotation;
                cube.getEntitiesOfClass(LivingEntity.class, level()).forEach((i) -> {
                    if (i != owner) {
                        DamageSource damagesource = null;
                        if (owner instanceof LivingEntity) {
                            damagesource = this.damageSources().source(DamageTypeRegistry.ancientProjectile, this, owner);
                            owner.setLastHurtMob(i);
                        }
                        i.hurt(damagesource, 5);
                    }
                });
                shot = true;
            }
            if (time >= maxTime) {
                remove(RemovalReason.KILLED);
            }
        }
    }

    private static Vec2 calculateRotationVector(Vec3 pVec, Vec3 pTarget) {
        double d0 = pTarget.x - pVec.x;
        double d1 = pTarget.y - pVec.y;
        double d2 = pTarget.z - pVec.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return new Vec2(
                Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)))),
                Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F)
        );
    }

    public Vec3 getEnd() {
        return end;
    }

    private HitResult getHit(double hitDistance, boolean hitFluids) {
        Vec3 vec3 = this.position();
        Vec3 vec31 = this.getDeltaMovement().normalize();
        Vec3 vec32 = vec3.add(vec31.x * hitDistance, vec31.y * hitDistance, vec31.z * hitDistance);
        return this.level().clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, hitFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this));
    }
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    public static Color getColor(Level level) {
        return Color.getHSBColor((float) (level.getGameTime() % 100) / 100f, 1f, 1f);
    }

    private static class ClientMethods {
        public static void particle(RandomSource random, Vec3 pos, Level level) {
            for (int i = 0; i < 25; i++) {
                Vec3 dir = new Vec3(Mth.nextFloat(random, -1f, 1f), Mth.nextFloat(random, -1f, 1f), Mth.nextFloat(random, -1f, 1f)).normalize().multiply(0.25f, 0.25f, 0.25f);
                level.addParticle(new CircleParticleOptions().setColor(getColor(level)).setAdditive(true), pos.x, pos.y, pos.z, dir.x, dir.y, dir.z);
            }
        }
    }
}
