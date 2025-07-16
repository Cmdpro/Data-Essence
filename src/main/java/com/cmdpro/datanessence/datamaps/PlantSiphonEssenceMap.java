package com.cmdpro.datanessence.datamaps;

import com.cmdpro.datanessence.DataNEssence;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public record PlantSiphonEssenceMap(int ticks, float amountPerTick) {
    public static final Codec<PlantSiphonEssenceMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("ticks").forGetter(PlantSiphonEssenceMap::ticks),
            Codec.FLOAT.fieldOf("amount_per_tick").forGetter(PlantSiphonEssenceMap::amountPerTick)
    ).apply(instance, PlantSiphonEssenceMap::new));
}