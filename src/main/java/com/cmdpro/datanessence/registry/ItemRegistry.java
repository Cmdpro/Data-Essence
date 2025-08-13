package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.EssenceShard;
import com.cmdpro.datanessence.api.item.FuelItem;
import com.cmdpro.datanessence.item.*;
import com.cmdpro.datanessence.item.equipment.*;
import com.cmdpro.datanessence.item.lens.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, DataNEssence.MOD_ID);
    // Lenses
    public static final Supplier<Item> HARMING_LENS = register("harming_lens", () -> new HarmingLens(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> HEALING_LENS = register("healing_lens", () -> new HealingLens(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ACCELERATION_LENS = register("acceleration_lens", () -> new AccelerationLens(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> BURNING_LENS = register("burning_lens", () -> new BurningLens(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> PRECISION_LENS = register("precision_lens", () -> new PrecisionLens(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ATTRACTING_LENS = register("attracting_lens", () -> new AttractingLens(new Item.Properties().stacksTo(1)));

    // Tools
    public static final Supplier<Item> ESSENCE_REDIRECTOR = register("essence_redirector", () -> new EssenceRedirector(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> DATA_DRIVE = register("data_drive", () -> new DataDrive(new Item.Properties()));
    public static final Supplier<Item> MOLD_ROD = register("rod_mold", () -> new Item(new Item.Properties().component(DataComponentRegistry.MOLD, DataNEssence.locate("metal_shaping/conductance_rod"))));
    public static final Supplier<Item> MOLD_PANEL = register("panel_mold", () -> new Item(new Item.Properties().component(DataComponentRegistry.MOLD, DataNEssence.locate("metal_shaping/capacitance_panel"))));
    public static final Supplier<Item> ECLIPTRUM_MOLD_ROD = register("rod_mold_ecliptrum", () -> new Item(new Item.Properties().component(DataComponentRegistry.MOLD, DataNEssence.locate("metal_shaping/transformative_rod"))));
    public static final Supplier<Item> ECLIPTRUM_MOLD_PANEL = register("panel_mold_ecliptrum", () -> new Item(new Item.Properties().component(DataComponentRegistry.MOLD, DataNEssence.locate("metal_shaping/reflective_panel"))));
    public static final Supplier<Item> THERMOMETER = register("thermometer", () -> new Thermometer(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> LOCATOR = register("locator", () -> new Locator(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ESSENCE_METER = register("essence_meter", () -> new EssenceMeter(new Item.Properties().stacksTo(1)));

    // Equipment
    public static final Supplier<Item> ESSENCE_SWORD = register("essence_sword", () -> new EssenceSword(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> PRIMITIVE_ANTI_GRAVITY_PACK = register("primitive_anti_gravity_pack", () -> new PrimitiveAntiGravityPack(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> WARP_CAPSULE = register("warp_capsule", () -> new WarpCapsule(new Item.Properties()));
    public static final Supplier<Item> ILLUMINATION_ROD = register("illumination_rod", () -> new IlluminationRod(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> REPULSION_ROD = register("repulsion_rod", () -> new RepulsionRod(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> TRAVERSITE_TRUDGERS = register("traversite_trudgers", () -> new TraversiteTrudgers(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> HAMMER_AND_CHISEL = register("hammer_and_chisel", () -> new HammerAndChisel(new Item.Properties().stacksTo(1).durability(256)));
    public static final Supplier<Item> GRAPPLING_HOOK = register("transcendental_tether", () -> new GrapplingHook(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> TRANS_GRAPPLING_HOOK = register("trans_tether", () -> new TransGrapplingHook(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ANTI_GRAVITY_PACK = register("anti_gravity_pack", () -> new AntiGravityPack(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ORE_SCANNER = register("mineral_finding_rod", () -> new OreScanner(new Item.Properties().stacksTo(1)));

    // Essence Shards
    public static final Supplier<Item> ESSENCE_SHARD = register("essence_shard", () -> new EssenceShard(new Item.Properties(), Map.of(EssenceTypeRegistry.ESSENCE, 100f)));
    public static final Supplier<Item> LUNAR_ESSENCE_SHARD = register("lunar_essence_shard", () -> new EssenceShard(new Item.Properties(), Map.of(EssenceTypeRegistry.LUNAR_ESSENCE, 100f)));

    // Wires
    public static final Supplier<Item> ESSENCE_WIRE = register("essence_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> LUNAR_ESSENCE_WIRE = register("lunar_essence_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> NATURAL_ESSENCE_WIRE = register("natural_essence_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> EXOTIC_ESSENCE_WIRE = register("exotic_essence_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ITEM_WIRE = register("item_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> FLUID_WIRE = register("fluid_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CHEMICAL_WIRE = register("chemical_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> RF_WIRE = register("rf_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> DATA_TABLET = register("data_tablet", () -> new DataTablet(new Item.Properties().stacksTo(1)));

    // Bombs
    public static final Supplier<Item> ESSENCE_BOMB = register("essence_bomb", () -> new EssenceBombItem(new Item.Properties()));
    public static final Supplier<Item> LUNAR_ESSENCE_BOMB = register("lunar_essence_bomb", () -> new LunarEssenceBombItem(new Item.Properties()));
    public static final Supplier<Item> NATURAL_ESSENCE_BOMB = register("natural_essence_bomb", () -> new NaturalEssenceBombItem(new Item.Properties()));
    public static final Supplier<Item> EXOTIC_ESSENCE_BOMB = register("exotic_essence_bomb", () -> new ExoticEssenceBombItem(new Item.Properties()));

    // Crafting Components
    public static final Supplier<Item> CONDUCTANCE_ROD = register("conductance_rod", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CAPACITANCE_PANEL = register("capacitance_panel", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> LOGICAL_MATRIX = register("logical_matrix", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> LENSING_CRYSTAL = register("lensing_crystal", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> LENS = register("lens", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> PROPELLER = register("propeller", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> WIRE_SPOOL = register("wire_spool", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BONDING_POWDER = register("bonding_powder", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> IRON_DRILL = register("iron_drill", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> EXCITER = register("exciter", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> HEATING_COIL = register("heating_coil", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> COPPER_SHELL = register("copper_shell", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ECLIPTRUM_INGOT = register("ecliptrum_ingot", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> TRANSFORMATIVE_ROD = register("transformative_rod", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> REFLECTIVE_PANEL = register("reflective_panel", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ECLIPTRUM_COG = register("ecliptrum_cog", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ECLIPTRUM_COG_ASSEMBLY_2X = register("ecliptrum_cog_assembly_2x", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ECLIPTRUM_COG_ASSEMBLY_4X = register("ecliptrum_cog_assembly_4x", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> EXTRICATION_ROD = register("extrication_rod", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> FROZEN_MOONLIGHT_CHUNK = register("frozen_moonlight_chunk", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> TRANSFORMATIVE_GEL = register("transformative_gel", () -> new Item(new Item.Properties()));

    // Ore Processing Intermediates and Nuggets
    public static final Supplier<Item> COPPER_NUGGET = register("copper_nugget", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> DIAMOND_SHARD = register("diamond_shard", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> EMERALD_SHARD = register("emerald_shard", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> COAL_LUMP = register("coal_lump", () -> new FuelItem(new Item.Properties(), 200));

    // Misc
    public static final Supplier<Item> COGNIZANT_CUBE = register("cognizant_cube", () -> new CognizantCube(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> MUSIC_DISC_PLAYER = register("music_disc_player", () -> new MusicDiscPlayer(new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)));
    public static final Supplier<Item> TRAVERSITE_ROAD_CHUNK = register("traversite_road_chunk", () -> new TraversiteRoadChunk(new Item.Properties()));
    public static final Supplier<Item> SHRINK_RAY = register("shrink_ray", () -> new ShrinkRay(new Item.Properties()));

    // Node Upgrades
    public static final Supplier<Item> SPEED_UPGRADE = register("speed_upgrade", () -> new SpeedNodeUpgrade(new Item.Properties(), 1));
    public static final Supplier<Item> SPEED_UPGRADE_2 = register("speed_upgrade_2", () -> new SpeedNodeUpgrade(new Item.Properties(), 3));
    public static final Supplier<Item> FILTER_UPGRADE = register("filter_upgrade", () -> new FilterNodeUpgrade(new Item.Properties().component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)));

    // Music Discs
    public static final Supplier<Item> UNDER_THE_SKY_MUSIC_DISC = register("under_the_sky_music_disc", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxSongRegistry.UNDER_THE_SKY)));

    // "Sprite" items, whose only use should be for rendering a given icon somewhere that only items are allowed to
    public static final Supplier<Item> SPRITE_BOOK_FLORA = register("sprite_book_flora", () -> new Item(new Item.Properties()));

    private static <T extends Item> Supplier<T> register(final String name, final Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
