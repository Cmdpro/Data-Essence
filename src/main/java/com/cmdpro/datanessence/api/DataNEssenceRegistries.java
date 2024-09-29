package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.computer.ComputerFileType;
import com.cmdpro.datanessence.api.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.datanessence.api.databank.MinigameSerializer;
import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = DataNEssence.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataNEssenceRegistries {
    public static ResourceKey<Registry<PageSerializer>> PAGE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "page_types"));
    public static ResourceKey<Registry<MinigameSerializer>> MINIGAME_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "minigames"));
    public static ResourceKey<Registry<ComputerFileType>> COMPUTER_FILE_TYPES_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "computer_file_types"));
    public static ResourceKey<Registry<MultiblockPredicateSerializer>> MULTIBLOCK_PREDICATE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "multiblock_predicates"));
    public static ResourceKey<Registry<EssenceType>> ESSENCE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "essence_types"));
    public static Registry<PageSerializer> PAGE_TYPE_REGISTRY = new RegistryBuilder<>(PAGE_TYPE_REGISTRY_KEY).sync(true).create();
    public static Registry<MinigameSerializer> MINIGAME_TYPE_REGISTRY = new RegistryBuilder<>(MINIGAME_TYPE_REGISTRY_KEY).sync(true).create();
    public static Registry<ComputerFileType> COMPUTER_FILE_TYPES_REGISTRY = new RegistryBuilder<>(COMPUTER_FILE_TYPES_REGISTRY_KEY).sync(true).create();
    public static Registry<MultiblockPredicateSerializer> MULTIBLOCK_PREDICATE_REGISTRY = new RegistryBuilder<>(MULTIBLOCK_PREDICATE_REGISTRY_KEY).sync(true).create();
    public static Registry<EssenceType> ESSENCE_TYPE_REGISTRY = new RegistryBuilder<>(ESSENCE_TYPE_REGISTRY_KEY).sync(true).create();
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(PAGE_TYPE_REGISTRY);
        event.register(MINIGAME_TYPE_REGISTRY);
        event.register(COMPUTER_FILE_TYPES_REGISTRY);
        event.register(MULTIBLOCK_PREDICATE_REGISTRY);
        event.register(ESSENCE_TYPE_REGISTRY);
    }
}
