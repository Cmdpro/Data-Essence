package com.cmdpro.datanessence.datamaps;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = DataNEssence.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataNEssenceDatamaps {
    public static final DataMapType<Item, PlantSiphonEssenceMap> PLANT_SIPHON_ESSENCE = DataMapType.builder(
            DataNEssence.locate("plant_siphon_essence"),
            Registries.ITEM,
            PlantSiphonEssenceMap.CODEC
    ).synced(PlantSiphonEssenceMap.CODEC, true).build();
    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(PLANT_SIPHON_ESSENCE);
    }
}
