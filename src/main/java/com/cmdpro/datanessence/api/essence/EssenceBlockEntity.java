package com.cmdpro.datanessence.api.essence;

import net.minecraft.core.Direction;

public interface EssenceBlockEntity {
    EssenceStorage getStorage();
    default float getMeterRenderOffset(Direction direction) {
        return 0.5f;
    }
    default float getMeterSideLength(Direction direction) {
        if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) {
            return 0.5f;
        }
        return 0.25f;
    }
    default EssenceBarBackgroundType getMeterBackgroundType() {
        return EssenceBarBackgroundTypes.INDUSTRIAL;
    }
}
