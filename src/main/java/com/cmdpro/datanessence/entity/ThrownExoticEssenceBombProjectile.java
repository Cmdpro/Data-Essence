package com.cmdpro.datanessence.entity;

import com.cmdpro.databank.misc.ColorGradient;
import com.cmdpro.datanessence.registry.EntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownExoticEssenceBombProjectile extends ThrownTrailItemProjectile {

    public ThrownExoticEssenceBombProjectile(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public ThrownExoticEssenceBombProjectile(Level pLevel, double pX, double pY, double pZ) {
        super(EntityRegistry.EXOTIC_ESSENCE_BOMB.get(), pX, pY, pZ, pLevel);
    }

    public ThrownExoticEssenceBombProjectile(LivingEntity pShooter, Level pLevel) {
        super(EntityRegistry.EXOTIC_ESSENCE_BOMB.get(), pShooter, pLevel);
    }
    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.EXOTIC_ESSENCE_BOMB.get();
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!level().isClientSide) {
            level().explode(this, pResult.getLocation().x, pResult.getLocation().y, pResult.getLocation().z, 4, Level.ExplosionInteraction.TNT);
            BlackHole blackHole = new BlackHole(EntityRegistry.BLACK_HOLE.get(), level());
            blackHole.setPos(pResult.getLocation().subtract(0, 2, 0));
            blackHole.size = 8;
            level().addFreshEntity(blackHole);
            remove(RemovalReason.KILLED);
        }
    }
    @Override
    public ColorGradient getGradient() {
        return EssenceTypeRegistry.EXOTIC_ESSENCE.get().getThrowGradient();
    }
}
