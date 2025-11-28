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
import java.util.*;
import java.util.List;

public class RifleLaser extends Entity {
    public LivingEntity owner;
    public int time;
    public final int maxTime = 50; // Increased slightly to allow the beam to finish travel
    int r;
    int damage;
    private static final EntityDataAccessor<Integer> SYNCHED_DISTANCE = SynchedEntityData.defineId(RifleLaser.class, EntityDataSerializers.INT);

    // -- QUEUE SETTINGS --
    private final Queue<BlockPos> blockQueue = new ArrayDeque<>();

    // Controls the "Speed" of the destruction wave.
    // 5 = Slow drill effect. 50 = Near instant.
    private static final int BLOCKS_PER_TICK = 50;
    // --------------------

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
        this.r = r;
        this.damage = damage;
        this.entityData.set(SYNCHED_DISTANCE, distance);
    }

    public RifleLaser(EntityType<RifleLaser> entityType, LivingEntity shooter, Level world, int r, int damage, int distance, boolean overcharged) {
        this(entityType, shooter.getX(), shooter.getEyeY(), shooter.getZ(), world);
        this.owner = shooter;
        this.setDeltaMovement(shooter.getLookAngle());
        this.r = r;
        this.damage = damage;
        this.entityData.set(SYNCHED_DISTANCE, distance);
        if(overcharged){
            DamageSource laserDamage = shooter.damageSources().source(
                    DamageTypeRegistry.laser,
                    shooter,
                    shooter
            );
            shooter.hurt(laserDamage, 20f);
            this.damage = 30;
            this.r = 10;
            this.entityData.set(SYNCHED_DISTANCE, 0);
        }
    }

    private boolean shot;
    private Vec3 end;

    @Override
    public void tick() {
        super.tick();
        int currentDistance = this.entityData.get(SYNCHED_DISTANCE);
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
                scanPathAndPopulateQueue(position(), end);
                shot = true;
            }

            processBlockQueue();

            // Keep entity alive until the blocks are done breaking
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

        double step = 0.9;

        for (double d = 0; d <= distance; d += step) {
            Vec3 point = start.add(direction.scale(d));
            double ox = (random.nextDouble() - 0.5) * 0.4;
            double oy = (random.nextDouble() - 0.5) * 0.4;
            double oz = (random.nextDouble() - 0.5) * 0.4;

            level().addParticle(
                    ParticleRegistry.ENERGY_PARTICLE.get(),
                    point.x + ox, point.y + oy, point.z + oz,
                    0, 0, 0
            );
        }
    }

    private void scanPathAndPopulateQueue(Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start).normalize();
        double distance = start.distanceTo(end);

        // CHANGE: Use LinkedHashSet to preserve the "Path" order (Closest to Furthest)
        Set<BlockPos> orderedBlocksToBreak = new LinkedHashSet<>();
        Set<LivingEntity> damagedEntities = new HashSet<>();

        double step = 0.5;
        // This loop goes from 0 (start) to distance (end), creating the path order
        for (double d = 0; d <= distance; d += step) {
            Vec3 point = start.add(direction.scale(d));
            BlockPos centerPos = BlockPos.containing(point);

            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    for (int dz = -r; dz <= r; dz++) {
                        double distSq = dx * dx + dy * dy + dz * dz;
                        if (distSq <= r*r) {
                            if (random.nextFloat() < 0.4f) {
                                // LinkedHashSet adds this to the END of the list.
                                // Since 'd' increases, we add further blocks later.
                                orderedBlocksToBreak.add(centerPos.offset(dx, dy, dz));
                            }
                        }
                    }
                }
            }

            AABB searchBox = new AABB(point.subtract(r, r, r), point.add(r, r, r));
            for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, searchBox)) {
                if (entity != owner && !damagedEntities.contains(entity)) {
                    double entityDist = entity.position().distanceTo(point);
                    if (entityDist <= r) {
                        DamageSource damagesource = this.damageSources().source(DamageTypeRegistry.magicProjectile, this, owner);
                        if (owner != null) owner.setLastHurtMob(entity);
                        entity.hurt(damagesource, this.damage);
                        damagedEntities.add(entity);
                    }
                }
            }
        }

        blockQueue.addAll(orderedBlocksToBreak);
    }

    private void processBlockQueue() {
        if (blockQueue.isEmpty()) return;

        int processed = 0;
        while (processed < BLOCKS_PER_TICK && !blockQueue.isEmpty()) {
            BlockPos pos = blockQueue.poll(); // Pulls the oldest (closest) block first

            if (pos != null && level().isLoaded(pos)) {
                BlockState state = level().getBlockState(pos);
                if (!state.isAir() && state.getDestroySpeed(level(), pos) >= 0) {
                    level().destroyBlock(pos, false);
                    spawnBlockBreakParticles(pos);
                    processed++;
                }
            }
        }
    }

    private void spawnBlockBreakParticles(BlockPos pos) {
        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 3; i++) { // Reduced count slightly for performance
                double px = pos.getX() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.4;
                double py = pos.getY() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.4;
                double pz = pos.getZ() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.4;

                double vx = (serverLevel.random.nextDouble() - 0.5) * 0.1;
                double vy = (serverLevel.random.nextDouble()) * 0.1;
                double vz = (serverLevel.random.nextDouble() - 0.5) * 0.1;

                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, px, py, pz, 1, vx, vy, vz, 0.05);
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

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SYNCHED_DISTANCE, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

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