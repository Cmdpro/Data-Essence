package com.cmdpro.datanessence.api.temperature;

public class BlockEntityTemperatureData {
    public final int overheatTemperature;
    public final int cooldownSpeed;
    public int temperature;
    public int temperatureSpeed;
    public int targetTemperature;
    public BlockEntityTemperatureData(int overheatTemperature, int cooldownSpeed) {
        this.overheatTemperature = overheatTemperature;
        this.cooldownSpeed = cooldownSpeed;
    }
    public void tick(TemperatureBlockEntity entity) {
        if (targetTemperature > temperature) {
            temperature += temperatureSpeed;
            if (targetTemperature < temperature) {
                temperature = targetTemperature;
            }
        } else if (targetTemperature < temperature) {
            temperature -= temperatureSpeed;
            if (targetTemperature > temperature) {
                temperature = targetTemperature;
            }
        }
        if (temperature >= overheatTemperature) {
            entity.overheat();
        }
    }
}
