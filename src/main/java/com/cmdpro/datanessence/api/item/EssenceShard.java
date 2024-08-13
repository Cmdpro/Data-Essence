package com.cmdpro.datanessence.api.item;

import net.minecraft.world.item.Item;

public class EssenceShard extends Item {
    public float essenceAmount;
    public float lunarEssenceAmount;
    public float naturalEssenceAmount;
    public float exoticEssenceAmount;
    public EssenceShard(Properties pProperties, float essenceAmount, float lunarEssenceAmount, float naturalEssenceAmount, float exoticEssenceAmount) {
        super(pProperties);
        this.essenceAmount = essenceAmount;
        this.lunarEssenceAmount = lunarEssenceAmount;
        this.naturalEssenceAmount = naturalEssenceAmount;
        this.exoticEssenceAmount = exoticEssenceAmount;
    }
}
