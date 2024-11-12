package com.cmdpro.datanessence.api.temperature;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;

public interface TemperatureSensitiveEssenceBlockEntity extends EssenceBlockEntity {

    /** Returns the current temperature of the block. */
    public int getTemperature();

    /** Returns the maximum temperature this block can handle - temperature is either capped to this,
     * or the block overheats, depending on the block. */
    public int getMaxTemperature();

    public int overheat();
}
