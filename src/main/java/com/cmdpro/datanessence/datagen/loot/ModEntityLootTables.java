package com.cmdpro.datanessence.datagen.loot;

import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;

import java.util.ArrayList;
import java.util.stream.Stream;

public class ModEntityLootTables extends EntityLootSubProvider {
    public ModEntityLootTables() {
        super(FeatureFlags.REGISTRY.allFlags());
    }


    @Override
    public void generate() {

    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return new ArrayList<EntityType<?>>().stream();//EntityInit.ENTITY_TYPES.getEntries().stream().map(RegistryObject::get);
    }
}