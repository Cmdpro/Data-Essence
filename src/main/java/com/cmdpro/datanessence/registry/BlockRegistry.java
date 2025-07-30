package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.block.SpreadingPlant;

import com.cmdpro.datanessence.block.DirectionalPillarBlock;
import com.cmdpro.datanessence.block.auxiliary.*;
import com.cmdpro.datanessence.block.decoration.Dewlamp;
import com.cmdpro.datanessence.block.decoration.EssenceBridge;
import com.cmdpro.datanessence.block.decoration.FlareLight;
import com.cmdpro.datanessence.block.decoration.LightFixture;
import com.cmdpro.datanessence.block.generation.EssenceBurner;
import com.cmdpro.datanessence.block.generation.EssenceLeech;
import com.cmdpro.datanessence.block.generation.IndustrialPlantSiphon;
import com.cmdpro.datanessence.block.processing.*;
import com.cmdpro.datanessence.block.production.*;
import com.cmdpro.datanessence.block.storage.*;
import com.cmdpro.datanessence.block.technical.Arekko;
import com.cmdpro.datanessence.block.technical.Computer;
import com.cmdpro.datanessence.block.technical.AncientDataBank;
import com.cmdpro.datanessence.block.technical.StructureProtector;
import com.cmdpro.datanessence.block.technical.cryochamber.Cryochamber;
import com.cmdpro.datanessence.block.technical.cryochamber.CryochamberRouter;
import com.cmdpro.datanessence.block.technical.empty_cryochamber.EmptyCryochamber;
import com.cmdpro.datanessence.block.technical.empty_cryochamber.EmptyCryochamberRouter;
import com.cmdpro.datanessence.block.transmission.*;
import com.cmdpro.datanessence.block.transmission.EssencePoint;
import com.cmdpro.datanessence.block.transmission.ExoticEssencePoint;
import com.cmdpro.datanessence.block.transmission.LunarEssencePoint;
import com.cmdpro.datanessence.block.transmission.NaturalEssencePoint;
import com.cmdpro.datanessence.block.transportation.*;
import com.cmdpro.datanessence.block.world.*;
import com.cmdpro.datanessence.block.world.shieldingstone.*;
import com.cmdpro.datanessence.integration.mekanism.ChemicalNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.GameMasterBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockRegistry {
    public static BlockBehaviour.Properties getMachineProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f);
    }

    public static BlockBehaviour.Properties getShieldlessAncientRockProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).mapColor(MapColor.COLOR_PURPLE).sound(SoundType.DEEPSLATE_TILES).explosionResistance(360000f);
    }

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK,
            DataNEssence.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = ItemRegistry.ITEMS;

    // World
    public static final Supplier<Block> ESSENCE_CRYSTAL = register("essence_crystal",
            () -> new EssenceCrystal(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> LENSING_CRYSTAL_ORE = register("lensing_crystal_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SPIRE_GLASS = register("spire_glass",
            () -> new TransparentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> TETHERGRASS = register("tethergrass",
            () -> new SpreadingPlant(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS).lightLevel((blockState) -> { return 5;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> CRYSTALLINE_LOG = register("crystalline_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> CRYSTALLINE_LEAVES = register("crystalline_leaves",
            () -> new CrystallineLeaves(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).emissiveRendering(BlockRegistry::always).lightLevel(state -> 5).sound(SoundType.AMETHYST)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Generators
    public static final Supplier<Block> ESSENCE_BURNER = register("essence_burner",
            () -> new EssenceBurner(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ESSENCE_LEECH = register("essence_leech",
            () -> new EssenceLeech(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> INDUSTRIAL_PLANT_SIPHON = register("industrial_plant_siphon",
            () -> new IndustrialPlantSiphon(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Machines
    public static final Supplier<Block> FABRICATOR = register("fabricator",
            () -> new Fabricator(getMachineProperties().lightLevel((blockState) -> { return 4;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> INFUSER = register("infuser",
            () -> new Infuser(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> FLUID_COLLECTOR = register("fluid_collector",
            () -> new FluidCollector(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> FLUID_MIXER = register("fluid_mixer",
            () -> new FluidMixer(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> MINERAL_PURIFICATION_CHAMBER = register("mineral_purification_chamber",
            () -> new MineralPurificationChamber(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> VACUUM = register("vacuum",
            () -> new Vacuum(getMachineProperties().lightLevel((blockState) -> { return 3;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> LASER_EMITTER = register("laser_emitter",
            () -> new LaserEmitter(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> FLUID_BOTTLER = register("fluid_bottler",
            () -> new FluidBottler(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ENTROPIC_PROCESSOR = register("entropic_processor",
            () -> new EntropicProcessor(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SYNTHESIS_CHAMBER = register("synthesis_chamber",
            () -> new SynthesisChamber(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ESSENCE_FURNACE = register("essence_furnace",
            () -> new EssenceFurnace(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> FLUID_SPILLER = register("fluid_spiller",
            () -> new FluidSpiller(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> CHARGER = register("charger",
            () -> new Charger(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> AUTO_FABRICATOR = register("auto-fabricator",
            () -> new AutoFabricator(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ENTICING_LURE = register("enticing_lure",
            () -> new EnticingLure(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ITEM_FILTER = register("item_filter",
            () -> new ItemFilter(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ESSENCE_BREAKER = register("essence_breaker",
            () -> new EssenceBreaker(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> METAL_SHAPER = register("metal_shaper",
            () -> new MetalShaper(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> MELTER = register("melter",
            () -> new Melter(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> DRYING_TABLE = register("drying_table",
            () -> new DryingTable(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> DATA_BANK = register("player_data_bank",
            () -> new DataBank(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().lightLevel((blockState) -> { return 4;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Buffers
    public static final Supplier<Block> ESSENCE_BUFFER = register("essence_buffer",
            () -> new EssenceBuffer(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ITEM_BUFFER = register("item_buffer",
            () -> new ItemBuffer(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> LIMITED_ITEM_BUFFER = register("limited_item_buffer",
            () -> new LimitedItemBuffer(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> FLUID_BUFFER = register("fluid_buffer",
            () -> new FluidBuffer(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> DECO_ESSENCE_BUFFER = register("deco_essence_buffer",
            () -> new RotatedPillarBlock(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> DECO_FLUID_BUFFER = register("deco_fluid_buffer",
            () -> new RotatedPillarBlock(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> DECO_ITEM_BUFFER = register("deco_item_buffer",
            () -> new RotatedPillarBlock(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Nodes
    public static final Supplier<Block> ESSENCE_POINT = register("essence_point",
            () -> new EssencePoint(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> LUNAR_ESSENCE_POINT = register("lunar_essence_point",
            () -> new LunarEssencePoint(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> NATURAL_ESSENCE_POINT = register("natural_essence_point",
            () -> new NaturalEssencePoint(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> EXOTIC_ESSENCE_POINT = register("exotic_essence_point",
            () -> new ExoticEssencePoint(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> FLUID_POINT = register("fluid_point",
            () -> new FluidPoint(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ITEM_POINT = register("item_point",
            () -> new ItemPoint(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> CHEMICAL_NODE = register("chemical_node",
            () -> new ChemicalNode(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Storage
    public static final Supplier<Block> ESSENCE_BATTERY = register("essence_battery",
            () -> new EssenceBattery(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> LUNAR_ESSENCE_BATTERY = register("lunar_essence_battery",
            () -> new LunarEssenceBattery(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> NATURAL_ESSENCE_BATTERY = register("natural_essence_battery",
            () -> new NaturalEssenceBattery(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> EXOTIC_ESSENCE_BATTERY = register("exotic_essence_battery",
            () -> new ExoticEssenceBattery(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> CREATIVE_ESSENCE_BATTERY = register("creative_essence_battery",
            () -> new CreativeEssenceBattery(getMachineProperties()),
            object -> () -> new GameMasterBlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> FLUID_TANK = register("fluid_tank",
            () -> new FluidTank(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Technical Blocks
    public static final Supplier<Block> STRUCTURE_PROTECTOR = register("structure_protector",
            () -> new StructureProtector(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).noOcclusion().instabreak().noCollission()),
            object -> () -> new GameMasterBlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_DATA_BANK = register("data_bank",
            () -> new AncientDataBank(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f, 3600000.0F).lightLevel((blockState) -> { return 4;})),
            object -> () -> new GameMasterBlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> COMPUTER = register("computer",
            () -> new Computer(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f, 3600000.0F).lightLevel((blockState) -> { return 4;})),
            object -> () -> new GameMasterBlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> AREKKO = registerBlock("arekko",
            () -> new Arekko(BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK).noOcclusion().noCollission()));
    public static final Supplier<Block> CRYOCHAMBER = register("cryochamber",
            () -> new Cryochamber(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> CRYOCHAMBER_FILLER = registerBlock("cryochamber_filler",
            () -> new CryochamberRouter(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion()));
    public static final Supplier<Block> ESSENCE_BRIDGE = register("essence_bridge",
            () -> new EssenceBridge(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(-1.0F, 3600000.0F).noOcclusion()),
            object -> () -> new GameMasterBlockItem(object.get(), new Item.Properties()));

    // Transportation Blocks
    public static final Supplier<Block> TRAVERSITE_ROAD = register("traversite_road",
            () -> new TraversiteRoad(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion().strength(2.0f), 2),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> TRAVERSITE_ROAD_STAIRS = register("traversite_road_stairs",
            () -> new TraversiteStairs(TRAVERSITE_ROAD.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion().strength(2.0f), 2),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> TRAVERSITE_ROAD_SLAB = register("traversite_road_slab",
            () -> new TraversiteSlab(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion().strength(2.0f), 2),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> TRAVERSITE_ROAD_OPAL = register("traversite_road_opal",
            () -> new TraversiteRoad(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion().strength(2.0f), 2),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> TRAVERSITE_ROAD_STAIRS_OPAL = register("traversite_road_stairs_opal",
            () -> new TraversiteStairs(TRAVERSITE_ROAD.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion().strength(2.0f), 2),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> TRAVERSITE_ROAD_SLAB_OPAL = register("traversite_road_slab_opal",
            () -> new TraversiteSlab(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion().strength(2.0f), 2),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ENDER_PEARL_CAPTURE = register("ender_pearl_capture",
            () -> new EnderPearlCapture(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ENDER_PEARL_DESTINATION = register("ender_pearl_destination",
            () -> new EnderPearlDestination(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ENDER_PEARL_RELAY = register("ender_pearl_relay",
            () -> new EnderPearlRelay(getMachineProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));


    // Shielding Rock - Structure-Only
    public static final Supplier<Block> ANCIENT_ROCK_BRICKS = register("ancient_rock_bricks",
            AncientShieldingStone::new,
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_ROCK_TILES = register("ancient_rock_tiles",
            AncientShieldingStone::new,
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_ROCK_COLUMN = register("ancient_rock_column",
            AncientShieldingPillar::new,
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ENERGIZED_ANCIENT_ROCK_COLUMN = register("energized_ancient_rock_column",
            () -> new LuminousAncientShieldingPillar(3),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_LANTERN = register("ancient_lantern",
            () -> new LuminousAncientShieldingStone(13),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_SHELF = register("ancient_shelf",
            AncientShieldingStone::new,
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_WINDOW = register("ancient_window",
            AncientShieldingGlass::new,
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_GLYPH_STONE_BLANK = register("ancient_glyph_stone_blank",
            AncientShieldingStone::new,
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_GLYPH_STONE_MAKUTUIN = register("ancient_glyph_stone_makutuin",
            AncientShieldingStone::new,
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_GLYPH_STONE_ESSENCE = register("ancient_glyph_stone_essence",
            AncientShieldingStone::new,
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Ancient Rock - Survival-Obtainable
    public static final Supplier<Block> SHIELDLESS_ANCIENT_ROCK_BRICKS = register("ancient_rock_bricks_shieldless",
            () -> new Block(getShieldlessAncientRockProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SHIELDLESS_ANCIENT_ROCK_TILES = register("ancient_rock_tiles_shieldless",
            () -> new Block(getShieldlessAncientRockProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SHIELDLESS_ANCIENT_ROCK_COLUMN = register("ancient_rock_column_shieldless",
            () -> new DirectionalPillarBlock(getShieldlessAncientRockProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SHIELDLESS_ENERGIZED_ANCIENT_ROCK_COLUMN = register("energized_ancient_rock_column_shieldless",
            () -> new DirectionalPillarBlock(getShieldlessAncientRockProperties().lightLevel((blockState) -> { return 3;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SHIELDLESS_ANCIENT_LANTERN = register("ancient_lantern_shieldless",
            () -> new Block(getShieldlessAncientRockProperties().lightLevel((blockState) -> { return 13;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SHIELDLESS_ANCIENT_SHELF = register("ancient_shelf_shieldless",
            () -> new Block(getShieldlessAncientRockProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SHIELDLESS_ANCIENT_WINDOW = register("ancient_window_shieldless",
            () -> new TransparentBlock(getShieldlessAncientRockProperties().noOcclusion().isViewBlocking(BlockRegistry::never).isSuffocating(BlockRegistry::never).isValidSpawn(BlockRegistry::never).isRedstoneConductor(BlockRegistry::never)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SHIELDLESS_ANCIENT_GLYPH_STONE_BLANK = register("ancient_glyph_stone_blank_shieldless",
            () -> new Block(getShieldlessAncientRockProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SHIELDLESS_ANCIENT_GLYPH_STONE_ESSENCE = register("ancient_glyph_stone_essence_shieldless",
            () -> new Block(getShieldlessAncientRockProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> SHIELDLESS_ANCIENT_GLYPH_STONE_MAKUTUIN = register("ancient_glyph_stone_makutuin_shieldless",
            () -> new Block(getShieldlessAncientRockProperties()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Obsidian Deco
    public static final Supplier<Block> POLISHED_OBSIDIAN = register("polished_obsidian",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> POLISHED_OBSIDIAN_COLUMN = register("polished_obsidian_column",
            () -> new DirectionalPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ENGRAVED_POLISHED_OBSIDIAN = register("engraved_polished_obsidian",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> POLISHED_OBSIDIAN_BRICKS = register("polished_obsidian_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> POLISHED_OBSIDIAN_TRACT = register("polished_obsidian_tract",
            () -> new DirectionalPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> POLISHED_OBSIDIAN_TILES = register("polished_obsidian_tiles",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> OBSIDIAN_FRAMED_GLASS = register("obsidian_framed_glass",
            () -> new TransparentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion().isViewBlocking(BlockRegistry::never).isSuffocating(BlockRegistry::never).isValidSpawn(BlockRegistry::never).isRedstoneConductor(BlockRegistry::never)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> AETHER_RUNE = register("aether_rune",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> LIGHT_FIXTURE = register("essence_light_fixture",
            () -> new LightFixture(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).instabreak().noOcclusion().noCollission().lightLevel((blockState) -> { return 15;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Copper Deco
    public static final Supplier<Block> PATTERNED_COPPER = register("patterned_copper",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> FLUIDIC_GLASS = register("fluidic_glass",
            () -> new TransparentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Misc Deco
    public static final Supplier<Block> FLARE_LIGHT = registerBlock("flare_light",
            () -> new FlareLight(
                    BlockBehaviour.Properties.of()
                            .replaceable()
                            .strength(-1.0F, 3600000.8F)
                            .mapColor(MapColor.NONE)
                            .lightLevel((state) -> 15)
                            .noLootTable()
                            .sound(SoundType.AMETHYST)
                            .noOcclusion().instabreak().noCollission()));
    public static final Supplier<Block> DEWLAMP = register("dewlamp",
            () -> new Dewlamp(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).noOcclusion().noCollission().lightLevel((blockState) -> { return 5;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> EMPTY_CRYOCHAMBER = register("empty_cryochamber",
            () -> new EmptyCryochamber(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion()),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> EMPTY_CRYOCHAMBER_ROUTER = registerBlock("empty_cryochamber_router",
            () -> new EmptyCryochamberRouter(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion()));
    private static <T extends Block> Supplier<T> registerBlock(final String name,
                                                                     final Supplier<? extends T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> Supplier<T> register(final String name, final Supplier<? extends T> block,
                                                                Function<Supplier<T>, Supplier<? extends Item>> item) {
        Supplier<T> obj = registerBlock(name, block);
        ITEMS.register(name, item.apply(obj));
        return obj;
    }

    public static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType<?> entityType) {
        return false;
    }

    public static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }

    public static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }
}
