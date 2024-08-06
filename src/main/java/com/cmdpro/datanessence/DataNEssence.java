package com.cmdpro.datanessence;

import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.networking.ModMessages;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod("datanessence")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = DataNEssence.MOD_ID)
public class DataNEssence
{
    public static final ResourceKey<DamageType> magicProjectile = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "magic_projectile"));
    public static final ResourceKey<DamageType> laser = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "laser"));
    public static final ResourceKey<DamageType> blackHole = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "black_hole"));
    public static final ResourceKey<DamageType> essenceSiphoned = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "essence_siphoned"));
    public static final String MOD_ID = "datanessence";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static RandomSource random;
    public DataNEssence(IEventBus bus)
    {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.getActiveContainer().registerConfig(ModConfig.Type.COMMON, DataNEssenceConfig.COMMON_SPEC, "datanessence.toml");
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
        ParticleRegistry.PARTICLE_TYPES.register(bus);
        AttachmentTypeRegistry.ATTACHMENT_TYPES.register(bus);
        ComputerFileTypeRegistry.COMPUTER_FILE_TYPES.register(bus);
        DataComponentRegistry.DATA_COMPONENTS.register(bus);
        ArmorMaterialRegistry.ARMOR_MATERIALS.register(bus);
        random = RandomSource.create();
    }
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ESSENCE_BURNER.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.FABRICATOR.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.INFUSER.get(), (o, direction) -> {
            if (direction == null) {
                return o.getCombinedHandler();
            }
            if (direction == Direction.DOWN) {
                return o.getItemHandler();
            }
            return o.getOutputHandler();
        });
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.AUTO_FABRICATOR.get(), (o, direction) -> {
            if (direction == null) {
                return o.getCombinedHandler();
            }
            if (direction == Direction.DOWN) {
                return o.getItemHandler();
            }
            return o.getOutputHandler();
        });
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ITEM_BUFFER.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ITEM_POINT.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_BUFFER.get(), (o, direction) -> o.getFluidHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_POINT.get(), (o, direction) -> o.getFluidHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_COLLECTOR.get(), (o, direction) -> o.getFluidHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_SPILLER.get(), (o, direction) -> o.getFluidHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.CHARGER.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.LASER_EMITTER.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.VACUUM.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_TANK.get(), (o, direction) -> o.getFluidHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.FLUID_BOTTLER.get(), (o, direction) -> {
            if (direction == null) {
                return o.getCombinedHandler();
            }
            if (direction == Direction.DOWN) {
                return o.getItemHandler();
            }
            return o.getOutputHandler();
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_BOTTLER.get(), (o, direction) -> o.getFluidHandler());
    }
    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {

        }
        if (event.getTabKey() == CreativeModeTabRegistry.getKey(CreativeModeTabRegistry.ITEMS.get())) {
            event.accept(ItemRegistry.DATA_TABLET.get());
            event.accept(ItemRegistry.ESSENCE_WIRE.get());
            event.accept(ItemRegistry.LUNAR_ESSENCE_WIRE.get());
            event.accept(ItemRegistry.NATURAL_ESSENCE_WIRE.get());
            event.accept(ItemRegistry.EXOTIC_ESSENCE_WIRE.get());
            event.accept(ItemRegistry.ITEM_WIRE.get());
            event.accept(ItemRegistry.FLUID_WIRE.get());
            event.accept(ItemRegistry.MAGIC_WRENCH.get());
            event.accept(ItemRegistry.DATA_DRIVE.get());
            event.accept(ItemRegistry.ESSENCE_SHARD.get());
            event.accept(ItemRegistry.ESSENCE_BOMB.get());
            event.accept(ItemRegistry.LUNAR_ESSENCE_BOMB.get());
            event.accept(ItemRegistry.NATURAL_ESSENCE_BOMB.get());
            event.accept(ItemRegistry.EXOTIC_ESSENCE_BOMB.get());
            event.accept(ItemRegistry.CAPACITANCE_PANEL.get());
            event.accept(ItemRegistry.CONDUCTANCE_ROD.get());
            event.accept(ItemRegistry.LOGICAL_MATRIX.get());
            event.accept(ItemRegistry.LENSING_CRYSTAL.get());
            event.accept(ItemRegistry.LENS.get());
            event.accept(ItemRegistry.MAGITECH_8_BALL.get());
            event.accept(ItemRegistry.HARMING_LENS.get());
            event.accept(ItemRegistry.HEALING_LENS.get());
            event.accept(ItemRegistry.ACCELERATION_LENS.get());
            event.accept(ItemRegistry.BURNING_LENS.get());
            event.accept(ItemRegistry.PRECISION_LENS.get());
            event.accept(ItemRegistry.PRIMITIVE_ANTI_GRAVITY_PACK.get());
        }
        if (event.getTabKey() == CreativeModeTabRegistry.getKey(CreativeModeTabRegistry.BLOCKS.get())) {
            event.accept(ItemRegistry.FABRICATOR_ITEM.get());
            event.accept(ItemRegistry.ESSENCE_POINT_ITEM.get());
            event.accept(ItemRegistry.LUNAR_ESSENCE_POINT_ITEM.get());
            event.accept(ItemRegistry.NATURAL_ESSENCE_POINT_ITEM.get());
            event.accept(ItemRegistry.EXOTIC_ESSENCE_POINT_ITEM.get());
            event.accept(ItemRegistry.ITEM_POINT_ITEM.get());
            event.accept(ItemRegistry.FLUID_POINT_ITEM.get());
            event.accept(BlockRegistry.ESSENCE_BUFFER.get());
            event.accept(BlockRegistry.ITEM_BUFFER.get());
            event.accept(BlockRegistry.FLUID_BUFFER.get());
            event.accept(BlockRegistry.DATA_BANK.get());
            event.accept(BlockRegistry.ESSENCE_CRYSTAL.get());
            event.accept(BlockRegistry.ESSENCE_BURNER.get());
            event.accept(BlockRegistry.TRAVERSITE_ROAD.get());
            event.accept(BlockRegistry.POLISHED_OBSIDIAN.get());
            event.accept(BlockRegistry.POLISHED_OBSIDIAN_COLUMN.get());
            event.accept(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN.get());
            event.accept(BlockRegistry.PATTERNED_COPPER.get());
            event.accept(BlockRegistry.ANCIENT_ROCK_COLUMN.get());
            event.accept(BlockRegistry.ENERGIZED_ANCIENT_ROCK_COLUMN.get());
            event.accept(BlockRegistry.ANCIENT_LANTERN.get());
            event.accept(BlockRegistry.ANCIENT_ROCK_BRICKS.get());
            event.accept(BlockRegistry.ANCIENT_ROCK_TILES.get());
            event.accept(BlockRegistry.DECO_ESSENCE_BUFFER.get());
            event.accept(BlockRegistry.DECO_ITEM_BUFFER.get());
            event.accept(BlockRegistry.DECO_FLUID_BUFFER.get());
            event.accept(ItemRegistry.INFUSER_ITEM.get());
            event.accept(BlockRegistry.COMPUTER.get());
            event.accept(BlockRegistry.FLUID_COLLECTOR.get());
            event.accept(BlockRegistry.FLUID_SPILLER.get());
            event.accept(BlockRegistry.LASER_EMITTER.get());
            event.accept(BlockRegistry.VACUUM.get());
            event.accept(BlockRegistry.ESSENCE_LEECH.get());
            event.accept(BlockRegistry.ESSENCE_BATTERY.get());
            event.accept(BlockRegistry.LUNAR_ESSENCE_BATTERY.get());
            event.accept(BlockRegistry.NATURAL_ESSENCE_BATTERY.get());
            event.accept(BlockRegistry.EXOTIC_ESSENCE_BATTERY.get());
            event.accept(BlockRegistry.FLUID_TANK.get());
            event.accept(BlockRegistry.LENSING_CRYSTAL_ORE.get());
            event.accept(ItemRegistry.AUTO_FABRICATOR_ITEM.get());
            event.accept(ItemRegistry.CHARGER_ITEM.get());
            event.accept(BlockRegistry.FLUID_BOTTLER.get());
        }
    }
}
