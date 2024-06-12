package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.init.EntityInit;
import com.cmdpro.datanessence.init.ItemInit;
import com.cmdpro.datanessence.misc.LunarEssenceBombExplosion;
import com.cmdpro.datanessence.misc.NaturalEssenceBombExplosion;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownNaturalEssenceBombProjectile extends ThrowableItemProjectile {
    public ThrownNaturalEssenceBombProjectile(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public ThrownNaturalEssenceBombProjectile(LivingEntity pShooter, Level pLevel) {
        super(EntityInit.NATURALESSENCEBOMB.get(), pShooter, pLevel);
    }
    @Override
    protected Item getDefaultItem() {
        return ItemInit.NATURALESSENCEBOMB.get();
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        Explosion.BlockInteraction explosion$blockinteraction1 = level().getGameRules().getBoolean(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;

        Explosion.BlockInteraction explosion$blockinteraction = explosion$blockinteraction1;
        Explosion explosion = new NaturalEssenceBombExplosion(level(), this, null, null, pResult.getLocation().x, pResult.getLocation().y, pResult.getLocation().z, 5, false, explosion$blockinteraction);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(level(), explosion)) return;
        explosion.explode();
        explosion.finalizeExplosion(true);
    }
}
