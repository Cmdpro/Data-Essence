package com.cmdpro.datanessence.api.temperature;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityTemperatureData {
    public final int overheatTemperature;
    public float temperature;
    public BlockEntityTemperatureData(int overheatTemperature) {
        this.overheatTemperature = overheatTemperature;
    }
    public void tick(TemperatureBlockEntity entity) {
        int heating = entity.getHeating();
        int cooling = entity.getCooling();
        int minTemp = entity.getMinimumTemperature();
        temperature += heating;
        if (temperature > minTemp) {
            temperature -= cooling;
            if (temperature < minTemp) {
                temperature = minTemp;
            }
        }
        if (temperature >= overheatTemperature) {
            entity.overheat();
        }
    }
}
