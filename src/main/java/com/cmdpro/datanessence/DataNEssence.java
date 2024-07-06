package com.cmdpro.datanessence;

import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.networking.ModMessages;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("datanessence")
@Mod.EventBusSubscriber(modid = DataNEssence.MOD_ID)
public class DataNEssence
{
    public static final ResourceKey<DamageType> magicProjectile = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(DataNEssence.MOD_ID, "magic_projectile"));
    public static final ResourceKey<DamageType> blackHole = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(DataNEssence.MOD_ID, "black_hole"));
    public static final String MOD_ID = "datanessence";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static RandomSource random;
    public DataNEssence()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, DataNEssenceConfig.COMMON_SPEC, "datanessence.toml");
        ItemRegistry.ITEMS.register(bus);
        MenuRegistry.MENUS.register(bus);
        RecipeRegistry.RECIPES.register(bus);
        RecipeRegistry.RECIPE_TYPES.register(bus);
        CreativeModeTabRegistry.CREATIVE_MODE_TABS.register(bus);
        PageTypeRegistry.PAGE_TYPES.register(bus);
        EntityRegistry.ENTITY_TYPES.register(bus);
        BlockRegistry.BLOCKS.register(bus);
        BlockEntityRegistry.BLOCK_ENTITIES.register(bus);
        MinigameRegistry.MINIGAME_TYPES.register(bus);
        FeatureRegistry.FEATURES.register(bus);
        GeckoLib.initialize();
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreative);
        random = RandomSource.create();
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {

        }
        if (event.getTabKey() == CreativeModeTabRegistry.ITEMS.getKey()) {
            event.accept(ItemRegistry.DATA_TABLET);
            event.accept(ItemRegistry.ESSENCE_WIRE);
            event.accept(ItemRegistry.LUNAR_ESSENCE_WIRE);
            event.accept(ItemRegistry.NATURAL_ESSENCE_WIRE);
            event.accept(ItemRegistry.EXOTIC_ESSENCE_WIRE);
            event.accept(ItemRegistry.ITEM_WIRE);
            event.accept(ItemRegistry.FLUID_WIRE);
            event.accept(ItemRegistry.MAGIC_WRENCH);
            event.accept(ItemRegistry.DATA_DRIVE);
            event.accept(ItemRegistry.ESSENCE_SHARD);
            event.accept(ItemRegistry.ESSENCE_BOMB);
            event.accept(ItemRegistry.LUNAR_ESSENCE_BOMB);
            event.accept(ItemRegistry.NATURAL_ESSENCE_BOMB);
            event.accept(ItemRegistry.EXOTIC_ESSENCE_BOMB);
            event.accept(ItemRegistry.CAPACITANCE_PANEL);
            event.accept(ItemRegistry.CONDUCTANCE_ROD);
            event.accept(ItemRegistry.LOGICAL_MATRIX);
        }
        if (event.getTabKey() == CreativeModeTabRegistry.BLOCKS.getKey()) {
            event.accept(ItemRegistry.FABRICATOR_ITEM);
            event.accept(ItemRegistry.ESSENCE_POINT_ITEM);
            event.accept(ItemRegistry.LUNAR_ESSENCE_POINT_ITEM);
            event.accept(ItemRegistry.NATURAL_ESSENCE_POINT_ITEM);
            event.accept(ItemRegistry.EXOTIC_ESSENCE_POINT_ITEM);
            event.accept(ItemRegistry.ITEM_POINT_ITEM);
            event.accept(ItemRegistry.FLUID_POINT_ITEM);
            event.accept(BlockRegistry.ESSENCE_BUFFER);
            event.accept(BlockRegistry.ITEM_BUFFER);
            event.accept(BlockRegistry.FLUID_BUFFER);
            event.accept(BlockRegistry.DATA_BANK);
            event.accept(BlockRegistry.ESSENCE_CRYSTAL);
            event.accept(BlockRegistry.ESSENCE_BURNER);
            event.accept(BlockRegistry.TRAVERSITE_ROAD);
            event.accept(BlockRegistry.POLISHED_OBSIDIAN);
            event.accept(BlockRegistry.POLISHED_OBSIDIAN_COLUMN);
            event.accept(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN);
            event.accept(BlockRegistry.PATTERNED_COPPER);
            event.accept(BlockRegistry.ANCIENT_ROCK_COLUMN);
            event.accept(BlockRegistry.ENERGIZED_ANCIENT_ROCK_COLUMN);
            event.accept(BlockRegistry.ANCIENT_LANTERN);
            event.accept(BlockRegistry.ANCIENT_ROCK_BRICKS);
            event.accept(BlockRegistry.ANCIENT_ROCK_TILES);
            event.accept(BlockRegistry.DECO_ESSENCE_BUFFER);
            event.accept(BlockRegistry.DECO_ITEM_BUFFER);
            event.accept(BlockRegistry.DECO_FLUID_BUFFER);
            event.accept(ItemRegistry.INFUSER_ITEM);
        }
    }
    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        ModMessages.register();
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Some example code to dispatch IMC to another mod
        //InterModComms.sendTo("datanessence", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {

        //Some example code to receive and process InterModComms from other mods
        /*
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).
                collect(Collectors.toList()));
        */
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

        // Do something when the server starts
    }


}
