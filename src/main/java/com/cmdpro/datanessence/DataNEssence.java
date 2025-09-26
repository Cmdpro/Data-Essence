package com.cmdpro.datanessence;

import com.cmdpro.datanessence.block.transmission.ItemFilter;
import com.cmdpro.datanessence.config.DataNEssenceClientConfig;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.integration.DataNEssenceIntegration;
import com.cmdpro.datanessence.registry.*;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.items.ComponentItemHandler;
import org.apache.commons.lang3.IntegerRange;
import org.slf4j.Logger;

import java.util.List;

import static com.cmdpro.datanessence.integration.DataNEssenceIntegration.*;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod("datanessence")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = DataNEssence.MOD_ID)
public class DataNEssence
{
    public static final String MOD_ID = "datanessence";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static RandomSource random;

    /**
     * Provides a ResourceLocation under DnE's namespace.
     */
    public static ResourceLocation locate(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public DataNEssence(IEventBus bus)
    {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.getActiveContainer().registerConfig(ModConfig.Type.COMMON, DataNEssenceConfig.COMMON_SPEC, "datanessence.toml");
        modLoadingContext.getActiveContainer().registerConfig(ModConfig.Type.CLIENT, DataNEssenceClientConfig.CLIENT_SPEC, "datanessence-client.toml");
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
        EssenceTypeRegistry.ESSENCE_TYPES.register(bus);
        MobEffectRegistry.MOB_EFFECTS.register(bus);
        SoundRegistry.SOUND_EVENTS.register(bus);
        FluidRegistry.FLUID_TYPES.register(bus);
        FluidRegistry.FLUIDS.register(bus);
        PotionRegistry.POTIONS.register(bus);
        HiddenConditionRegistry.HIDDEN_CONDITIONS.register(bus);
        random = RandomSource.create();

        DataNEssenceIntegration.init();

        if (FMLEnvironment.dist.isClient())
            modLoadingContext.getActiveContainer().registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.INFUSER.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : machine.getOutputHandler();
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.AUTO_FABRICATOR.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : machine.getOutputHandler();
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ITEM_BUFFER.get(), (o, direction) -> o.getItemHandler());

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.LIMITED_ITEM_BUFFER.get(), (o, direction) -> o.getItemHandler());

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_BUFFER.get(), (o, direction) -> o.getFluidHandler());

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_COLLECTOR.get(), (o, direction) -> o.getFluidHandler());

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_SPILLER.get(), (o, direction) -> o.getFluidHandler());

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_TANK.get(), (o, direction) -> o.getFluidHandler());

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.FLUID_BOTTLER.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : machine.getOutputHandler();
        });

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_BOTTLER.get(), (o, direction) -> {
            return o.getFluidHandler();
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ENTROPIC_PROCESSOR.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : machine.getOutputHandler();
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ESSENCE_FURNACE.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : machine.getOutputHandler();
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.SYNTHESIS_CHAMBER.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : machine.getOutputHandler();
        });

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FLUID_MIXER.get(), (o, direction) -> {
            return o.getOutputHandler();
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.FLUID_MIXER.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : null;
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.MINERAL_PURIFICATION_CHAMBER.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : machine.getOutputHandler();
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ITEM_FILTER.get(), (o, direction) -> {
            if (direction == null) {
                return null;
            }
            IntegerRange range = ItemFilter.DIRECTIONS.get(direction);
            boolean canInsert = true;
            if (range != null) {
                for (int i = range.getMinimum(); i <= range.getMaximum(); i++) {
                    if (!o.getFilterHandler().getStackInSlot(i).isEmpty()) {
                        canInsert = false;
                    }
                }
                if (canInsert) {
                    return o.getItemHandler();
                }
            }
            return null;
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.METAL_SHAPER.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : machine.getOutputHandler();
        });

        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, 1), ItemRegistry.MUSIC_DISC_PLAYER.get());

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.MELTER.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : null;
        });

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.MELTER.get(), (machine, direction) -> {
            return machine.getOutputHandler();
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.DRYING_TABLE.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : machine.getOutputHandler();
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.CHARGER.get(), (o, direction) -> {
            if (direction == null) {
                return o.getCombinedInvWrapper();
            }
            return null;
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ENTICING_LURE.get(), (o, direction) -> {
            if (direction == null) {
                return o.getCombinedInvWrapper();
            }
            return null;
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.LASER_EMITTER.get(), (o, direction) -> {
            if (direction == null) {
                return o.getCombinedInvWrapper();
            }
            return null;
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ESSENCE_BURNER.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : null;
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.INDUSTRIAL_PLANT_SIPHON.get(), (machine, direction) -> {
            if (direction == null) {
                return machine.getCombinedInvWrapper();
            }
            return machine.getValidInputDirections().contains(direction) ? machine.getItemHandler() : null;
        });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.FABRICATOR.get(), (o, direction) -> {
            if (direction == null) {
                return o.getCombinedInvWrapper();
            }
            return null;
        });

    }
    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {

        }
        if (event.getTabKey() == CreativeModeTabRegistry.getKey(CreativeModeTabRegistry.ITEMS.get())) {
            // Tools
            event.accept(ItemRegistry.DATA_TABLET.get());
            event.accept(ItemRegistry.ESSENCE_REDIRECTOR.get());
            event.accept(ItemRegistry.LOCATOR.get());
            event.accept(ItemRegistry.DATA_DRIVE.get());
            event.accept(ItemRegistry.ESSENCE_METER.get());
            event.accept(ItemRegistry.MOLD_PANEL.get());
            event.accept(ItemRegistry.MOLD_ROD.get());
            //event.accept(ItemRegistry.THERMOMETER.get());
            event.accept(ItemRegistry.HAMMER_AND_CHISEL.get());
            event.accept(ItemRegistry.ORE_SCANNER.get());
            // Combat Tools
            event.accept(ItemRegistry.ESSENCE_BOMB.get());
            //event.accept(ItemRegistry.LUNAR_ESSENCE_BOMB.get());
            //event.accept(ItemRegistry.NATURAL_ESSENCE_BOMB.get());
            //event.accept(ItemRegistry.EXOTIC_ESSENCE_BOMB.get());
            event.accept(ItemRegistry.ESSENCE_SWORD.get());
            // Exploration Tools
            event.accept(ItemRegistry.WARP_CAPSULE.get());
            event.accept(ItemRegistry.ILLUMINATION_ROD.get());
            event.accept(ItemRegistry.PRIMITIVE_ANTI_GRAVITY_PACK.get());
            event.accept(ItemRegistry.TRAVERSITE_TRUDGERS.get());
            event.accept(ItemRegistry.GRAPPLING_HOOK.get());


            // Wires
            event.accept(ItemRegistry.ESSENCE_WIRE.get());
            //event.accept(ItemRegistry.LUNAR_ESSENCE_WIRE.get());
            //event.accept(ItemRegistry.NATURAL_ESSENCE_WIRE.get());
            //event.accept(ItemRegistry.EXOTIC_ESSENCE_WIRE.get());
            event.accept(ItemRegistry.ITEM_WIRE.get());
            event.accept(ItemRegistry.FLUID_WIRE.get());
            if (hasMekanism)
                event.accept(ItemRegistry.CHEMICAL_WIRE.get());

            // Upgrades
            event.accept(ItemRegistry.HARMING_LENS.get());
            event.accept(ItemRegistry.HEALING_LENS.get());
            event.accept(ItemRegistry.ACCELERATION_LENS.get());
            event.accept(ItemRegistry.ATTRACTING_LENS.get());
            event.accept(ItemRegistry.BURNING_LENS.get());
            event.accept(ItemRegistry.PRECISION_LENS.get());
            event.accept(ItemRegistry.SPEED_UPGRADE.get());
            event.accept(ItemRegistry.FILTER_UPGRADE.get());

            // Materials
            event.accept(ItemRegistry.ESSENCE_SHARD.get());
            event.accept(ItemRegistry.LENSING_CRYSTAL.get());
            event.accept(ItemRegistry.BONDING_POWDER.get());
            event.accept(ItemRegistry.CAPACITANCE_PANEL.get());
            event.accept(ItemRegistry.CONDUCTANCE_ROD.get());
            event.accept(ItemRegistry.WIRE_SPOOL.get());
            event.accept(ItemRegistry.COPPER_SHELL.get());
            event.accept(ItemRegistry.LOGICAL_MATRIX.get());
            event.accept(ItemRegistry.PROPELLER.get());
            event.accept(ItemRegistry.EXCITER.get());
            event.accept(ItemRegistry.HEATING_COIL.get());
            event.accept(ItemRegistry.IRON_DRILL.get());
            event.accept(ItemRegistry.LENS.get());
            event.accept(ItemRegistry.COPPER_NUGGET.get());
            event.accept(ItemRegistry.DIAMOND_SHARD.get());
            event.accept(ItemRegistry.EMERALD_SHARD.get());
            event.accept(ItemRegistry.COAL_LUMP.get());
            event.accept(ItemRegistry.TRAVERSITE_ROAD_CHUNK.get());

            // Buckets
            event.accept(FluidRegistry.GENDERFLUID.bucket.get());

            // Loot Items
            event.accept(ItemRegistry.COGNIZANT_CUBE.get());
            event.accept(ItemRegistry.MUSIC_DISC_PLAYER.get());
            event.accept(ItemRegistry.UNDER_THE_SKY_MUSIC_DISC.get());
        }
        if (event.getTabKey() == CreativeModeTabRegistry.getKey(CreativeModeTabRegistry.BLOCKS.get())) {
            // World
            event.accept(BlockRegistry.ESSENCE_CRYSTAL.get());
            event.accept(BlockRegistry.LENSING_CRYSTAL_ORE.get());
            event.accept(BlockRegistry.TETHERGRASS.get());
            //event.accept(BlockRegistry.SPIRE_GLASS.get());
            //event.accept(BlockRegistry.CRYSTALLINE_LOG.get());
            //event.accept(BlockRegistry.CRYSTALLINE_LEAVES.get());

            // Generators
            event.accept(BlockRegistry.ESSENCE_BURNER.get());
            event.accept(BlockRegistry.ESSENCE_LEECH.get());
            event.accept(BlockRegistry.INDUSTRIAL_PLANT_SIPHON.get());

            // Machines
            event.accept(BlockRegistry.FABRICATOR.get());
            event.accept(BlockRegistry.INFUSER.get());
            event.accept(BlockRegistry.FLUID_COLLECTOR.get());
            event.accept(BlockRegistry.FLUID_SPILLER.get());
            event.accept(BlockRegistry.FLUID_MIXER.get());
            event.accept(BlockRegistry.LASER_EMITTER.get());
            event.accept(BlockRegistry.VACUUM.get());
            event.accept(BlockRegistry.AUTO_FABRICATOR.get());
            event.accept(BlockRegistry.CHARGER.get());
            event.accept(BlockRegistry.FLUID_BOTTLER.get());
            event.accept(BlockRegistry.ENTROPIC_PROCESSOR.get());
            event.accept(BlockRegistry.ESSENCE_FURNACE.get());
            event.accept(BlockRegistry.SYNTHESIS_CHAMBER.get());
            event.accept(BlockRegistry.ENTICING_LURE.get());
            event.accept(BlockRegistry.MINERAL_PURIFICATION_CHAMBER.get());
            event.accept(BlockRegistry.ESSENCE_BREAKER.get());
            event.accept(BlockRegistry.METAL_SHAPER.get());
            event.accept(BlockRegistry.MELTER.get());
            event.accept(BlockRegistry.DRYING_TABLE.get());

            // Logistics
            event.accept(BlockRegistry.ITEM_FILTER.get());
            event.accept(BlockRegistry.ESSENCE_POINT.get());
            //event.accept(BlockRegistry.LUNAR_ESSENCE_POINT.get());
            //event.accept(BlockRegistry.NATURAL_ESSENCE_POINT.get());
            //event.accept(BlockRegistry.EXOTIC_ESSENCE_POINT.get());
            event.accept(BlockRegistry.ITEM_POINT.get());
            event.accept(BlockRegistry.FLUID_POINT.get());
            if (hasMekanism)
                event.accept(BlockRegistry.CHEMICAL_NODE.get());
            event.accept(BlockRegistry.ESSENCE_BUFFER.get());
            event.accept(BlockRegistry.ITEM_BUFFER.get());
            event.accept(BlockRegistry.FLUID_BUFFER.get());
            event.accept(BlockRegistry.LIMITED_ITEM_BUFFER.get());

            // Redstone Components
            event.accept(BlockRegistry.ESSENCE_READER.get());

            // Storage Blocks
            event.accept(BlockRegistry.ESSENCE_BATTERY.get());
            //event.accept(BlockRegistry.LUNAR_ESSENCE_BATTERY.get());
            //event.accept(BlockRegistry.NATURAL_ESSENCE_BATTERY.get());
            //event.accept(BlockRegistry.EXOTIC_ESSENCE_BATTERY.get());
            event.accept(BlockRegistry.FLUID_TANK.get());

            // Transportation Blocks
            event.accept(BlockRegistry.ENDER_PEARL_CAPTURE.get());
            event.accept(BlockRegistry.ENDER_PEARL_RELAY.get());
            event.accept(BlockRegistry.ENDER_PEARL_DESTINATION.get());
            event.accept(BlockRegistry.TRAVERSITE_ROAD.get());
            event.accept(BlockRegistry.TRAVERSITE_ROAD_STAIRS.get());
            event.accept(BlockRegistry.TRAVERSITE_ROAD_SLAB.get());
            if (hasOpalescence) {
                event.accept(BlockRegistry.TRAVERSITE_ROAD_OPAL.get());
                event.accept(BlockRegistry.TRAVERSITE_ROAD_STAIRS_OPAL.get());
                event.accept(BlockRegistry.TRAVERSITE_ROAD_SLAB_OPAL.get());
            }

            // Decoration
            event.accept(BlockRegistry.POLISHED_OBSIDIAN.get());
            event.accept(BlockRegistry.POLISHED_OBSIDIAN_BRICKS.get());
            event.accept(BlockRegistry.POLISHED_OBSIDIAN_TILES.get());
            event.accept(BlockRegistry.POLISHED_OBSIDIAN_COLUMN.get());
            event.accept(BlockRegistry.POLISHED_OBSIDIAN_TRACT.get());
            event.accept(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN.get());
            event.accept(BlockRegistry.OBSIDIAN_FRAMED_GLASS.get());
            event.accept(BlockRegistry.FLUIDIC_GLASS.get());
            event.accept(BlockRegistry.LIGHT_FIXTURE.get());
            event.accept(BlockRegistry.DEWLAMP.get());
            event.accept(BlockRegistry.PATTERNED_COPPER.get());
            event.accept(BlockRegistry.DECO_ESSENCE_BUFFER.get());
            event.accept(BlockRegistry.DECO_ITEM_BUFFER.get());
            event.accept(BlockRegistry.DECO_FLUID_BUFFER.get());

            // Structure Blocks
            event.accept(BlockRegistry.AETHER_RUNE.get());
            event.accept(BlockRegistry.ANCIENT_ROCK_COLUMN.get());
            event.accept(BlockRegistry.ENERGIZED_ANCIENT_ROCK_COLUMN.get());
            event.accept(BlockRegistry.ANCIENT_LANTERN.get());
            event.accept(BlockRegistry.ANCIENT_ROCK_BRICKS.get());
            event.accept(BlockRegistry.ANCIENT_ROCK_TILES.get());
            event.accept(BlockRegistry.ANCIENT_SHELF.get());
            event.accept(BlockRegistry.ANCIENT_WINDOW.get());
            event.accept(BlockRegistry.ANCIENT_GLYPH_STONE_BLANK.get());
            event.accept(BlockRegistry.ANCIENT_GLYPH_STONE_MAKUTUIN.get());
            event.accept(BlockRegistry.ANCIENT_GLYPH_STONE_ESSENCE.get());
            event.accept(BlockRegistry.SHIELDLESS_ANCIENT_ROCK_COLUMN.get());
            event.accept(BlockRegistry.SHIELDLESS_ENERGIZED_ANCIENT_ROCK_COLUMN.get());
            event.accept(BlockRegistry.SHIELDLESS_ANCIENT_LANTERN.get());
            event.accept(BlockRegistry.SHIELDLESS_ANCIENT_ROCK_BRICKS.get());
            event.accept(BlockRegistry.SHIELDLESS_ANCIENT_ROCK_TILES.get());
            event.accept(BlockRegistry.SHIELDLESS_ANCIENT_SHELF.get());
            event.accept(BlockRegistry.SHIELDLESS_ANCIENT_WINDOW.get());
            event.accept(BlockRegistry.SHIELDLESS_ANCIENT_GLYPH_STONE_BLANK.get());
            event.accept(BlockRegistry.SHIELDLESS_ANCIENT_GLYPH_STONE_MAKUTUIN.get());
            event.accept(BlockRegistry.SHIELDLESS_ANCIENT_GLYPH_STONE_ESSENCE.get());
            event.accept(BlockRegistry.COMPUTER.get());
            event.accept(BlockRegistry.ANCIENT_DATA_BANK.get());
            event.accept(BlockRegistry.STRUCTURE_PROTECTOR.get());
        }
    }
}
