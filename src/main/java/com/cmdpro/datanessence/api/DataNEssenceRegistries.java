package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.screen.databank.Minigame;
import com.cmdpro.datanessence.screen.databank.MinigameCreator;
import com.cmdpro.datanessence.screen.databank.MinigameSerializer;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.google.common.eventbus.EventBus;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = DataNEssence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataNEssenceRegistries {
    public static ResourceKey<Registry<PageSerializer>> PAGE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(DataNEssence.MOD_ID, "page_types"));
    public static ResourceKey<Registry<MinigameSerializer>> MINIGAME_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(DataNEssence.MOD_ID, "minigames"));
    public static Registry<PageSerializer> PAGE_TYPE_REGISTRY = new RegistryBuilder<>(PAGE_TYPE_REGISTRY_KEY).sync(true).create();
    public static Registry<MinigameSerializer> MINIGAME_TYPE_REGISTRY = new RegistryBuilder<>(MINIGAME_TYPE_REGISTRY_KEY).sync(true).create();
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(PAGE_TYPE_REGISTRY);
        event.register(MINIGAME_TYPE_REGISTRY);
    }
}
