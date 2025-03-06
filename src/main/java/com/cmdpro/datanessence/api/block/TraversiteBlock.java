package com.cmdpro.datanessence.api.block;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.resources.ResourceLocation;

public interface TraversiteBlock {
    ResourceLocation TRAVERSITE_ROAD_SPEED_UUID = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "traversite_road_speed");
    float getBoost();
}
