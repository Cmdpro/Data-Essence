package com.cmdpro.datanessence.entity;

import com.cmdpro.databank.impact.ImpactFrameHandler;
import com.cmdpro.databank.misc.CollisionTestCube;
import com.cmdpro.databank.misc.FloatGradient;
import com.cmdpro.databank.misc.ScreenshakeHandler;
import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.client.renderers.entity.RifleLaserRenderer;
import com.cmdpro.datanessence.registry.DamageTypeRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

public class RifleLaser extends Entity {
    public LivingEntity owner;
    public int time;
    public final int maxTime = 25;
    public RifleLaser(EntityType<RifleLaser> entityType, Level world) {
        super(entityType, world);
    }
    protected RifleLaser(EntityType<RifleLaser> entityType, double x, double y, double z, Level world) {
        this(entityType, world);
        this.setPos(x, y, z);
    }
    public RifleLaser(EntityType<RifleLaser> entityType, LivingEntity shooter, Level world) {
        this(entityType, shooter.getX(), shooter.getEyeY(), shooter.getZ(), world);
        this.owner = shooter;
        this.setDeltaMovement(shooter.getLookAngle());
    }

    private boolean shot;
    private Vec3 end;
    @Override
    public void tick() {
        super.tick();
        HitResult hit = getHit(35, false);
        end = hit.getLocation();
        time++;
        if (level().isClientSide) {
            if (!shot) {
                ClientMethods.particle(getRandom(), end, level());
                double distance = Math.min(ClientMethods.getPlayerDistance(position()), ClientMethods.getPlayerDistance(end));
                if (distance <= 35) {
                    ClientMethods.screenshake(end, level());
                    ClientMethods.impact(this);
                }
                shot = true;
            }
        } else {
            if (!shot) {
                CollisionTestCube cube = new CollisionTestCube(AABB.ofSize(position().lerp(end, 0.5f), 0.75, 0.75, position().distanceTo(end)*2f));
                Quaternionf rotation = new Quaternionf();
                Vec2 angle = calculateRotationVector(position(), end);
                rotation.rotateY((float) Math.toRadians(-angle.y + 180));
                rotation.rotateX((float)Math.toRadians(-angle.x));
                cube.rotation = rotation;
                cube.getEntitiesOfClass(LivingEntity.class, level()).forEach((i) -> {
                    if (i != owner) {
                        DamageSource damagesource = null;
                        if (owner instanceof LivingEntity) {
                            damagesource = this.damageSources().source(DamageTypeRegistry.magicProjectile, this, owner);
                            owner.setLastHurtMob(i);
                        }
                        float damage = 7.5f;
                        i.hurt(damagesource, damage);
                    }
                });
                level().explode(null, end.x, end.y, end.z, 4, Level.ExplosionInteraction.TNT);
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

    private static class ClientMethods {
        public static void particle(RandomSource random, Vec3 pos, Level level) {
            for (int i = 0; i < 75; i++) {
                Vec3 dir = new Vec3(Mth.nextFloat(random, -1f, 1f), Mth.nextFloat(random, -1f, 1f), Mth.nextFloat(random, -1f, 1f)).normalize().multiply(0.25f, 0.25f, 0.25f);
                level.addParticle(new CircleParticleOptions().setColor(new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color)).setAdditive(true), pos.x, pos.y, pos.z, dir.x, dir.y, dir.z);
            }
        }
        public static double getPlayerDistance(Vec3 pos) {
            return Minecraft.getInstance().player == null ? -1 : Minecraft.getInstance().player.getEyePosition().distanceTo(pos);
        }
        public static void screenshake(Vec3 pos, Level level) {
            ScreenshakeHandler.addScreenshake(24, 3f);
        }
        public static void impact(RifleLaser entity) {
            ImpactFrameHandler.addImpact(12, (renderTarget, buffer, poseStack, deltaTracker, camera, frustum, renderTicks, progress) -> {

            }, (renderTarget, buffer, poseStack, deltaTracker, camera, frustum, renderTicks, progress) -> {
                Vec3 center = entity.end;
                float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
                poseStack.pushPose();
                Vec3 pos = entity.position();
                poseStack.translate(pos.x, pos.y, pos.z);
                RifleLaserRenderer.renderBeamFor(entity, poseStack, buffer, partialTick);
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.translate(center.x, center.y, center.z);
                float shift = 20f;
                float modifiedProgress = progress;
                float slowStart = 0.5f;
                float slowEnd = 0.75f;
                float slowRange = 1f-slowStart;
                if (progress >= slowStart) {
                    float slowProgress = (progress-slowStart)/slowRange;
                    modifiedProgress = slowStart+((slowEnd-slowStart)*slowProgress);
                }
                float scaleAddition = modifiedProgress*shift;
                for (int i = 0; i < 16; i++) {
                    double angle = Math.toRadians((360f/16f)*i);
                    Vec3 dir = new Vec3(Math.sin(angle), 0, Math.cos(angle));
                    renderLine(dir.scale(Math.clamp(scaleAddition-2.5f, 0, scaleAddition)), dir.scale(scaleAddition), 0.05f, poseStack, buffer);
                }
                for (int i = 0; i < 16; i++) {
                    double angle = Math.toRadians((360f/16f)*i);
                    Vec3 dir = new Vec3(0, Math.sin(angle), Math.cos(angle));
                    renderLine(dir.scale(Math.clamp(scaleAddition-2.5f, 0, scaleAddition)), dir.scale(scaleAddition), 0.05f, poseStack, buffer);
                }
                for (int i = 0; i < 16; i++) {
                    double angle = Math.toRadians((360f/16f)*i);
                    Vec3 dir = new Vec3(Math.cos(angle), Math.sin(angle), 0);
                    renderLine(dir.scale(Math.clamp(scaleAddition-2.5f, 0, scaleAddition)), dir.scale(scaleAddition), 0.05f, poseStack, buffer);
                }
                poseStack.popPose();
            }, FloatGradient.singleValue(1f).fade(1, 0.65f, 0f, 1f));
        }
        private static void renderLine(Vec3 start, Vec3 end, float thickness, PoseStack poseStack, MultiBufferSource buffer) {
            RifleLaserRenderer.renderBeam(poseStack, buffer, start, end, thickness);
        }
    }
}
