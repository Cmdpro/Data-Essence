package com.cmdpro.datanessence.api.item;

import com.cmdpro.datanessence.block.auxiliary.LaserEmitterBlockEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface ILaserEmitterModule {
    default void applyToMob(LaserEmitterBlockEntity ent, LivingEntity entity) {}
    default int getRedstoneLevel(LaserEmitterBlockEntity ent, List<LivingEntity> entities) {
        return entities.size() > 0 ? 15 : 0;
    }

    /**
     * Determines the color of the laser beam when this lens is slotted into the block.
     * @return integer color
     */
    int getBeamColor();
}
