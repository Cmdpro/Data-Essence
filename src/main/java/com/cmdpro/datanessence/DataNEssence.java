package com.cmdpro.datanessence;

import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.init.*;
import com.cmdpro.datanessence.networking.ModMessages;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
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
    public static final ResourceKey<DamageType> magicProjectile = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(DataNEssence.MOD_ID, "magicprojectile"));
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

        ItemInit.ITEMS.register(bus);
        MenuInit.MENUS.register(bus);
        RecipeInit.RECIPES.register(bus);
        RecipeInit.RECIPE_TYPES.register(bus);
        CreativeModeTabInit.CREATIVE_MODE_TABS.register(bus);
        PageTypeInit.PAGE_TYPES.register(bus);
        BlockInit.BLOCKS.register(bus);
        BlockEntityInit.BLOCK_ENTITIES.register(bus);
        MinigameInit.MINIGAME_TYPES.register(bus);
        GeckoLib.initialize();
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreative);
        random = RandomSource.create();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, DataNEssenceConfig.COMMON_SPEC, "datanessence.toml");
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {

        }
        if (event.getTabKey() == CreativeModeTabInit.ITEMS.getKey()) {
            event.accept(ItemInit.DATATABLET);
            event.accept(ItemInit.ESSENCEWIRE);
            event.accept(ItemInit.LUNARESSENCEWIRE);
            event.accept(ItemInit.NATURALESSENCEWIRE);
            event.accept(ItemInit.EXOTICESSENCEWIRE);
            event.accept(ItemInit.ITEMWIRE);
            event.accept(ItemInit.FLUIDWIRE);
        }
        if (event.getTabKey() == CreativeModeTabInit.BLOCKS.getKey()) {
            event.accept(ItemInit.FABRICATORITEM);
            event.accept(ItemInit.ESSENCEPOINTITEM);
            event.accept(ItemInit.LUNARESSENCEPOINTITEM);
            event.accept(ItemInit.NATURALESSENCEPOINTITEM);
            event.accept(ItemInit.EXOTICESSENCEPOINTITEM);
            event.accept(ItemInit.ITEMPOINTITEM);
            event.accept(ItemInit.FLUIDPOINTITEM);
            event.accept(BlockInit.ESSENCEBUFFER);
            event.accept(BlockInit.ITEMBUFFER);
            event.accept(BlockInit.FLUIDBUFFER);
            event.accept(BlockInit.DATABANK);
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
