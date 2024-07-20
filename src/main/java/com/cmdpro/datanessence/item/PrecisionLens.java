package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.api.ILaserEmitterModule;
import com.cmdpro.datanessence.block.entity.LaserEmitterBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.joml.Math;

import java.util.List;

public class PrecisionLens extends Item implements ILaserEmitterModule {
    public PrecisionLens(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getRedstoneLevel(LaserEmitterBlockEntity ent, List<LivingEntity> entities) {
        return Math.clamp(0, 15, entities.size());
    }
}
