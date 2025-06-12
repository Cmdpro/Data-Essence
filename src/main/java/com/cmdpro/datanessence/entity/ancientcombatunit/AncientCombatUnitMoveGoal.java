package com.cmdpro.datanessence.entity.ancientcombatunit;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AncientCombatUnitMoveGoal extends Goal {
    protected final AncientCombatUnit mob;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private long lastCanUseCheck;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;

    public AncientCombatUnitMoveGoal(AncientCombatUnit mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.canMove()) {
            return false;
        }
        long i = this.mob.level().getGameTime();
        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (canPenalize) {
                    if (--this.ticksUntilNextPathRecalculation <= 0) {
                        this.path = this.mob.getNavigation().createPath(livingentity, 0);
                        this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                        return this.path != null;
                    } else {
                        return true;
                    }
                }
                this.path = this.mob.getNavigation().createPath(livingentity, 0);
                return this.path != null ? true : this.mob.isWithinMeleeAttackRange(livingentity);
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.mob.canMove()) {
            return false;
        }
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else if (!this.followingTargetEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
        } else {
            return !this.mob.isWithinRestriction(livingentity.blockPosition())
                    ? false
                    : !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player)livingentity).isCreative();
        }
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        LivingEntity livingentity = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
            this.mob.setTarget(null);
        }

        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingentity))
                    && this.ticksUntilNextPathRecalculation <= 0
                    && (
                    this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0
                            || livingentity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0
                            || this.mob.getRandom().nextFloat() < 0.05F
            )) {
                this.pathedTargetX = livingentity.getX();
                this.pathedTargetY = livingentity.getY();
                this.pathedTargetZ = livingentity.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                double d0 = this.mob.distanceToSqr(livingentity);
                if (this.canPenalize) {
                    this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
                    if (this.mob.getNavigation().getPath() != null) {
                        net.minecraft.world.level.pathfinder.Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
                        if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                            failedPathFindingPenalty = 0;
                        else
                            failedPathFindingPenalty += 10;
                    } else {
                        failedPathFindingPenalty += 10;
                    }
                }
                if (d0 > 1024.0) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d0 > 256.0) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                Vec3 pos = livingentity.position();
                Vec3 vecToEnt = pos.multiply(1, 0, 1).vectorTo(mob.position().multiply(1, 0, 1));
                pos = pos.subtract(vecToEnt.scale(3));
                if (!this.mob.getNavigation().moveTo(pos.x, pos.y, pos.z, this.speedModifier)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
    }
}
