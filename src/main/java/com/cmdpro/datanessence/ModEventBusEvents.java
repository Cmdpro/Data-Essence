package com.cmdpro.datanessence;

import com.cmdpro.datanessence.api.*;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.entity.AncientSentinel;
import com.cmdpro.datanessence.registry.EntityRegistry;
import com.cmdpro.datanessence.screen.databank.MinigameSerializer;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = DataNEssence.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.ANCIENT_SENTINEL.get(), AncientSentinel.setAttributes());
    }
    @SubscribeEvent
    public static void onModConfigEvent(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == DataNEssenceConfig.COMMON_SPEC) {
            DataNEssenceConfig.bake(config);
        }
    }
    @SubscribeEvent
    public static void entitySpawnRestriction(RegisterSpawnPlacementsEvent event) {
    }
}
