package com.cmdpro.datanessence.api.temperature;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;

public interface TemperatureBlockEntity extends EssenceBlockEntity {
    BlockEntityTemperatureData getTemperatureData();
    void overheat();
}
