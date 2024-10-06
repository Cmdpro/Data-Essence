package com.cmdpro.datanessence.api.misc;

import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ICustomEssencePointBehaviour {
    default boolean canExtractEssence(EssenceStorage storage, EssenceType type, float amount) {
        return true;
    }
    default boolean canInsertEssence(EssenceStorage storage, EssenceType type, float amount) {
        return true;
    }
}
