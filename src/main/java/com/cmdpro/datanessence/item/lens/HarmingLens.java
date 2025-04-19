package com.cmdpro.datanessence.item.lens;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.ILaserEmitterModule;
import com.cmdpro.datanessence.block.auxiliary.LaserEmitterBlockEntity;
import com.cmdpro.datanessence.registry.DamageTypeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class HarmingLens extends Item implements ILaserEmitterModule {
    public HarmingLens(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void applyToMob(LaserEmitterBlockEntity ent, LivingEntity entity) {
        entity.hurt(entity.damageSources().source(DamageTypeRegistry.laser), 2.5f);
    }

    @Override
    public int getBeamColor() {
        return 0x381657;
    }
}
