package com.cmdpro.datanessence.entity;

import com.cmdpro.databank.impact.ImpactFrameHandler;
import com.cmdpro.databank.misc.FloatGradient;
import com.cmdpro.databank.misc.ScreenshakeHandler;
import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.client.renderers.entity.RifleLaserRenderer;
import com.cmdpro.datanessence.registry.DamageTypeRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.ParticleRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class RifleLaser extends Entity {
    public LivingEntity owner;
    public int time;
    public final int maxTime = 50;
    int r;
    int damage;
    private static final EntityDataAccessor<Integer> SYNCHED_DISTANCE = SynchedEntityData.defineId(RifleLaser.class, EntityDataSerializers.INT); // please dont ask me how this works
    public RifleLaser(EntityType<RifleLaser> entityType, Level world) {
        super(entityType, world);
    }
    protected RifleLaser(EntityType<RifleLaser> entityType, double x, double y, double z, Level world) {
        this(entityType, world);
        this.setPos(x, y, z);
    }
    public RifleLaser(EntityType<RifleLaser> entityType, LivingEntity shooter, Level world, int r, int damage, int distance) {
        this(entityType, shooter.getX(), shooter.getEyeY(), shooter.getZ(), world);
        this.owner = shooter;
        this.setDeltaMovement(shooter.getLookAngle());
        this.r = r; // default: 3
        this.damage = damage; // default: 21
        this.entityData.set(SYNCHED_DISTANCE, distance); // default: 35
    }

    public RifleLaser(EntityType<RifleLaser> entityType, LivingEntity shooter, Level world, int r, int damage, int distance, boolean overcharged) {
        this(entityType, shooter.getX(), shooter.getEyeY(), shooter.getZ(), world);
        this.owner = shooter;
        this.setDeltaMovement(shooter.getLookAngle());
        this.r = r; // default: 3
        this.damage = damage; // default: 21
        this.entityData.set(SYNCHED_DISTANCE, distance); // default: 35
        if(overcharged==true){
            shooter.hurt(shooter.damageSources().magic(),20f);
            this.damage = 30;
            this.r = 10;
            this.entityData.set(SYNCHED_DISTANCE, 0);
        }
    }

    private boolean shot;
    private Vec3 end;
    private Set<BlockPos> brokenBlocks = new HashSet<>();


    @Override
    public void tick() {
        super.tick();
        int currentDistance = this.entityData.get(SYNCHED_DISTANCE);
        // Calculate end point without collision detection (pass through blocks)
        Vec3 vec3 = this.position();
        Vec3 vec31 = this.getDeltaMovement().normalize();
        end = vec3.add(vec31.x * currentDistance, vec31.y * currentDistance, vec31.z * currentDistance);

        time++;
        if (level().isClientSide) {

            spawnBeamParticles();

            if (!shot) {
                ClientMethods.particle(getRandom(), end, level());
                double lDistance = Math.min(ClientMethods.getPlayerDistance(position()), ClientMethods.getPlayerDistance(end));
                if (lDistance <= currentDistance) {
                    ClientMethods.screenshake(end, level());
                    ClientMethods.impact(this);
                }
                shot = true;
            }
        } else {
            if (!shot) {
                // Break blocks along the laser path and damage entities
                breakBlocksAndDamageEntitiesAlongPath(position(), end);
                shot = true;
            }
            if (time >= maxTime) {
                remove(RemovalReason.KILLED);
            }
        }
    }

    private void spawnBeamParticles() {
        if (end == null) return;

        Vec3 start = this.position();
        Vec3 direction = end.subtract(start).normalize();
        double distance = start.distanceTo(end);

        double step = 0.9; // particle spacing along the beam

        for (double d = 0; d <= distance; d += step) {
            Vec3 point = start.add(direction.scale(d));

            // Random offset around beam
            double ox = (random.nextDouble() - 0.5) * 0.4;
            double oy = (random.nextDouble() - 0.5) * 0.4;
            double oz = (random.nextDouble() - 0.5) * 0.4;

            level().addParticle(
                    ParticleRegistry.ENERGY_PARTICLE.get(),
                    point.x + ox,
                    point.y + oy,
                    point.z + oz,
                    0, 0, 0
            );
        }
    }


    private void breakBlocksAndDamageEntitiesAlongPath(Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start).normalize();
        double distance = start.distanceTo(end);
        Set<BlockPos> blocksToBreak = new HashSet<>();
        Set<LivingEntity> damagedEntities = new HashSet<>();

        // Sample points along the laser path
        double step = 0.5; // Check every 0.5 blocks
        for (double d = 0; d <= distance; d += step) {
            Vec3 point = start.add(direction.scale(d));
            BlockPos centerPos = BlockPos.containing(point);
            // Break blocks in a randomized 3-block radius around the laser
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    for (int dz = -r; dz <= r; dz++) {
                        double distSq = dx * dx + dy * dy + dz * dz;
                        if (distSq <= r*r) { // Radius of 3 blocks (3^2 = 9)
                            // Randomize whether this block gets destroyed (70% chance)
                            if (random.nextFloat() < 0.4f) {
                                blocksToBreak.add(centerPos.offset(dx, dy, dz));
                            }
                        }
                    }
                }
            }

            // Damage entities in the 3-block radius
            AABB searchBox = new AABB(point.subtract(r, r, r), point.add(r, r, r));
            for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, searchBox)) {
                if (entity != owner && !damagedEntities.contains(entity)) {
                    double entityDist = entity.position().distanceTo(point);
                    if (entityDist <= r) {
                        DamageSource damagesource = this.damageSources().source(DamageTypeRegistry.magicProjectile, this, owner);
                        if (owner != null) {
                            owner.setLastHurtMob(entity);
                        }
                        float damage = this.damage;
                        entity.hurt(damagesource, damage);
                        damagedEntities.add(entity);
                    }
                }
            }
        }

// Break all collected blocks without dropping items
        for (BlockPos pos : blocksToBreak) {
            BlockState state = level().getBlockState(pos);
            if (!state.isAir() && state.getDestroySpeed(level(), pos) >= 0) {

                // Break block (no drops)
                level().destroyBlock(pos, false);

                // Spawn cloud particles
                if (level() instanceof ServerLevel serverLevel) {
                    // spawn between 3–6 particles randomly
                    for (int i = 0; i < 5; i++) {
                        double px = pos.getX() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.4;
                        double py = pos.getY() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.4;
                        double pz = pos.getZ() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.4;

                        double vx = (serverLevel.random.nextDouble() - 0.5) * 0.1;
                        double vy = (serverLevel.random.nextDouble()) * 0.1;
                        double vz = (serverLevel.random.nextDouble() - 0.5) * 0.1;

                        serverLevel.sendParticles(
                                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                px, py, pz,
                                1,       // count per iteration
                                vx, vy, vz,
                                0.05     // speed
                        );
                    }
                }
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
        builder.define(SYNCHED_DISTANCE, 0);
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