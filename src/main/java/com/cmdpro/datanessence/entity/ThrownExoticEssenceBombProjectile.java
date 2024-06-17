package com.cmdpro.datanessence.entity;

import com.cmdpro.datanessence.init.EntityInit;
import com.cmdpro.datanessence.init.ItemInit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

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

}
