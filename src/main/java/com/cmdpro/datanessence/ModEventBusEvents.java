package com.cmdpro.datanessence;

import com.cmdpro.datanessence.api.*;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.init.RecipeInit;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = DataNEssence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {

    }
    @SubscribeEvent
    public static void onModConfigEvent(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == DataNEssenceConfig.COMMON_SPEC) {
            DataNEssenceConfig.bake(config);
        }
    }
    @SubscribeEvent
    public static void registerStuff(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_TYPES, helper -> {
        });
    }
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        DataNEssenceUtil.PAGE_TYPE_REGISTRY = event.create(new RegistryBuilder<PageSerializer>()
                .setName(new ResourceLocation(DataNEssence.MOD_ID, "pagetypes")));
    }
    @SubscribeEvent
    public static void entitySpawnRestriction(SpawnPlacementRegisterEvent event) {
    }
}
