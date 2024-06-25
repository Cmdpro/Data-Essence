package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.init.EntityInit;
import com.cmdpro.datanessence.init.ItemInit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownExoticEssenceBombProjectile extends ThrowableItemProjectile {

    public ThrownExoticEssenceBombProjectile(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ThrownExoticEssenceBombProjectile(LivingEntity pShooter, Level pLevel) {
        super(EntityInit.EXOTIC_ESSENCE_BOMB.get(), pShooter, pLevel);
    }
    @Override
    protected Item getDefaultItem() {
        return ItemInit.EXOTIC_ESSENCE_BOMB.get();
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        level().explode(this, pResult.getLocation().x, pResult.getLocation().y, pResult.getLocation().z, 4, Level.ExplosionInteraction.TNT);
        BlackHole blackHole = new BlackHole(EntityInit.BLACK_HOLE.get(), level());
        blackHole.setPos(pResult.getLocation().subtract(0, 2, 0));
        blackHole.getEntityData().set(BlackHole.SIZE, 8f);
        level().addFreshEntity(blackHole);
        remove(RemovalReason.KILLED);
    }
}
