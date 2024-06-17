package com.cmdpro.datanessence.misc;

import com.cmdpro.datanessence.block.EssenceCrystal;
import com.cmdpro.datanessence.init.BlockInit;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LunarEssenceBombExplosion extends Explosion {
    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
    private static final int MAX_DROPS_PER_COMBINED_STACK = 16;
    private final boolean fire;
    private final Explosion.BlockInteraction blockInteraction;
    private final RandomSource random = RandomSource.create();
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    @javax.annotation.Nullable
    private final Entity source;
    private final float radius;
    private final DamageSource damageSource;
    private final ExplosionDamageCalculator damageCalculator;
    private final ObjectArrayList<BlockPos> toBlow = new ObjectArrayList<>();
    private final Map<Player, Vec3> hitPlayers = Maps.newHashMap();
    private final Vec3 position;
    public LunarEssenceBombExplosion(Level pLevel, @javax.annotation.Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, List<BlockPos> pPositions) {
        this(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY, pPositions);
    }

    public LunarEssenceBombExplosion(Level pLevel, @javax.annotation.Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction, List<BlockPos> pPositions) {
        this(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
        this.toBlow.addAll(pPositions);
    }

    public LunarEssenceBombExplosion(Level pLevel, @javax.annotation.Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction) {
        this(pLevel, pSource, (DamageSource)null, (ExplosionDamageCalculator)null, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
    }

    public LunarEssenceBombExplosion(Level pLevel, @javax.annotation.Nullable Entity pSource, @javax.annotation.Nullable DamageSource pDamageSource, @Nullable ExplosionDamageCalculator pDamageCalculator, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
        this.level = pLevel;
        this.source = pSource;
        this.radius = pRadius;
        this.x = pToBlowX;
        this.y = pToBlowY;
        this.z = pToBlowZ;
        this.fire = pFire;
        this.blockInteraction = pBlockInteraction;
        this.damageSource = pDamageSource == null ? pLevel.damageSources().explosion(this) : pDamageSource;
        this.damageCalculator = pDamageCalculator == null ? this.makeDamageCalculator(pSource) : pDamageCalculator;
        this.position = new Vec3(this.x, this.y, this.z);
    }

    private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity pEntity) {
        return (ExplosionDamageCalculator)(pEntity == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator(pEntity));
    }
    @Override
    public void explode() {
        this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
                for(int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;

                        for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            BlockState blockstate = this.level.getBlockState(blockpos);
                            FluidState fluidstate = this.level.getFluidState(blockpos);
                            if (!this.level.isInWorldBounds(blockpos)) {
                                break;
                            }

                            Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this, this.level, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            }

                            d4 += d0 * (double)0.3F;
                            d6 += d1 * (double)0.3F;
                            d8 += d2 * (double)0.3F;
                        }
                    }
                }
            }
        }

        this.toBlow.addAll(set);
        float f2 = this.radius * 2.0F;
        int k1 = Mth.floor(this.x - (double)f2 - 1.0D);
        int l1 = Mth.floor(this.x + (double)f2 + 1.0D);
        int i2 = Mth.floor(this.y - (double)f2 - 1.0D);
        int i1 = Mth.floor(this.y + (double)f2 + 1.0D);
        int j2 = Mth.floor(this.z - (double)f2 - 1.0D);
        int j1 = Mth.floor(this.z + (double)f2 + 1.0D);
        List<Entity> list = this.level.getEntities(this.source, new AABB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.level, this, list, f2);
        Vec3 vec3 = new Vec3(this.x, this.y, this.z);

        for(int k2 = 0; k2 < list.size(); ++k2) {
            Entity entity = list.get(k2);
            if (!entity.ignoreExplosion()) {
                double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double)f2;
                if (d12 <= 1.0D) {
                    double d5 = entity.getX() - this.x;
                    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                    double d9 = entity.getZ() - this.z;
                    double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d13 != 0.0D) {
                        d5 /= d13;
                        d7 /= d13;
                        d9 /= d13;
                        double d14 = (double)getSeenPercent(vec3, entity);
                        double d10 = (1.0D - d12) * d14;
                        entity.hurt(this.getDamageSource(), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f2 + 1.0D)));
                        double d11;
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity)entity;
                            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, d10);
                            if (livingentity.isDeadOrDying()) {
                                boolean found = false;
                                for (int o = 0; o > -5; o--) {
                                    BlockPos blockPos = livingentity.blockPosition().offset(0, o, 0);
                                    if (level.getBlockState(blockPos).isAir()) {
                                        if (level.getBlockState(blockPos.below()).isCollisionShapeFullBlock(level, blockPos.below())) {
                                            found = true;
                                            level.setBlockAndUpdate(blockPos, BlockInit.ESSENCE_CRYSTAL.get().defaultBlockState().setValue(EssenceCrystal.FACING, Direction.UP).setValue(EssenceCrystal.MODEL, random.nextIntBetweenInclusive(0, 2)));
                                        } else {
                                            break;
                                        }
                                    }
                                }
                                if (!found) {
                                    LootTable loottable = ((ServerLevel)level).getServer().getLootData().getLootTable(BlockInit.ESSENCE_CRYSTAL.get().getLootTable());
                                    List<ItemStack> loot = loottable.getRandomItems(new LootParams.Builder((ServerLevel)level).withParameter(LootContextParams.BLOCK_STATE, BlockInit.ESSENCE_CRYSTAL.get().defaultBlockState()).create(LootContextParamSets.BLOCK));
                                    for (ItemStack o : loot) {
                                        livingentity.spawnAtLocation(o);
                                    }
                                }
                            }
                        } else {
                            d11 = d10;
                        }

                        d5 *= d11;
                        d7 *= d11;
                        d9 *= d11;
                        Vec3 vec31 = new Vec3(d5, d7, d9);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
                        if (entity instanceof Player) {
                            Player player = (Player)entity;
                            if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                this.hitPlayers.put(player, vec31);
                            }
                        }
                    }
                }
            }
        }
    }
}
