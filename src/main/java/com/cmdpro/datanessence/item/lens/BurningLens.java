package com.cmdpro.datanessence.item.lens;

import com.cmdpro.datanessence.api.item.ILaserEmitterModule;
import com.cmdpro.datanessence.block.auxiliary.LaserEmitterBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class BurningLens extends Item implements ILaserEmitterModule {
    public BurningLens(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void applyToMob(LaserEmitterBlockEntity ent, LivingEntity entity) {
        if (entity.getRemainingFireTicks() <= 25) {
            entity.setRemainingFireTicks(25);
        }
        entity.hurt(entity.damageSources().inFire(), 1);
    }

    @Override
    public int getBeamColor() {
        return 0xf5701d;
    }
}
