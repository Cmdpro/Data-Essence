package com.cmdpro.datanessence.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PlantSiphonEssenceMap(int ticks, float amountPerTick) {
    public static final Codec<PlantSiphonEssenceMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("ticks").forGetter(PlantSiphonEssenceMap::ticks),
            Codec.FLOAT.fieldOf("amount_per_tick").forGetter(PlantSiphonEssenceMap::amountPerTick)
    ).apply(instance, PlantSiphonEssenceMap::new));
}