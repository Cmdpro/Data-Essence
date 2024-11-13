package com.cmdpro.datanessence.api.temperature;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface TemperatureBlockEntity extends EssenceBlockEntity {
    BlockEntityTemperatureData getTemperatureData();
    void overheat();
    int getMinimumTemperature();
    int getCooling();
    int getHeating();
}
