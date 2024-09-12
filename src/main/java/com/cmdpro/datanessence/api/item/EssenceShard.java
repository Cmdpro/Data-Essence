package com.cmdpro.datanessence.api.item;

import com.cmdpro.datanessence.api.essence.EssenceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.function.Supplier;

public class EssenceShard extends Item {
    public Map<Supplier<EssenceType>, Float> essence;
    public EssenceShard(Properties pProperties, Map<Supplier<EssenceType>, Float> essence) {
        super(pProperties);
        this.essence = essence;
    }
}
