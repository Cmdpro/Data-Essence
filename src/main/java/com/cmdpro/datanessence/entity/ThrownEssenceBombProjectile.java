package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.init.EntityInit;
import com.cmdpro.datanessence.init.ItemInit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownEssenceBombProjectile extends ThrowableItemProjectile {
    public ThrownEssenceBombProjectile(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public ThrownEssenceBombProjectile(LivingEntity pShooter, Level pLevel) {
        super(EntityInit.ESSENCE_BOMB.get(), pShooter, pLevel);
    }
    @Override
    protected Item getDefaultItem() {
        return ItemInit.ESSENCE_BOMB.get();
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!level().isClientSide) {
            level().explode(this, pResult.getLocation().x, pResult.getLocation().y, pResult.getLocation().z, 4, Level.ExplosionInteraction.TNT);
            remove(RemovalReason.KILLED);
        }
    }
}
