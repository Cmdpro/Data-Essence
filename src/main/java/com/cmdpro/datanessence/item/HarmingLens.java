package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ILaserEmitterModule;
import com.cmdpro.datanessence.block.auxiliary.LaserEmitterBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class HarmingLens extends Item implements ILaserEmitterModule {
    public HarmingLens(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void applyToMob(LaserEmitterBlockEntity ent, LivingEntity entity) {
        entity.hurt(entity.damageSources().source(DataNEssence.laser), 2.5f);
    }
}
