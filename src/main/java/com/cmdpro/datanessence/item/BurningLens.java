package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ILaserEmitterModule;
import com.cmdpro.datanessence.block.entity.LaserEmitterBlockEntity;
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
}
