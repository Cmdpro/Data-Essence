package com.cmdpro.datanessence.api.node;

import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;

public interface ICustomEssencePointBehaviour {
    default boolean canExtractEssence(EssenceStorage from, EssenceStorage to, EssenceType type, float amount) {
        return true;
    }
    default boolean canInsertEssence(EssenceStorage from, EssenceStorage to, EssenceType type, float amount) {
        return true;
    }
}
