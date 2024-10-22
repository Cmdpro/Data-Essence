package com.cmdpro.datanessence;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.hiddenblock.HiddenBlockConditions;
import com.cmdpro.databank.hiddenblock.conditions.AdvancementCondition;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.entity.AncientSentinel;
import com.cmdpro.datanessence.hiddenblock.EntryCondition;
import com.cmdpro.datanessence.registry.EntityRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

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
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerProjectileBehavior(ItemRegistry.ESSENCE_BOMB.get());
            DispenserBlock.registerProjectileBehavior(ItemRegistry.LUNAR_ESSENCE_BOMB.get());
            DispenserBlock.registerProjectileBehavior(ItemRegistry.NATURAL_ESSENCE_BOMB.get());
            DispenserBlock.registerProjectileBehavior(ItemRegistry.EXOTIC_ESSENCE_BOMB.get());
        });
    }
}
