package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.*;
import com.cmdpro.datanessence.api.item.EssenceShard;
import com.cmdpro.datanessence.item.*;
import com.cmdpro.datanessence.item.blockitem.*;
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

    // Tools
    public static final Supplier<Item> MAGIC_WRENCH = register("magic_wrench", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> DATA_DRIVE = register("data_drive", () -> new DataDrive(new Item.Properties()));

    // Equipment
    public static final Supplier<Item> ESSENCE_SWORD = register("essence_sword", () -> new EssenceSword(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> PRIMITIVE_ANTI_GRAVITY_PACK = register("primitive_anti_gravity_pack", () -> new PrimitiveAntiGravityPack(new Item.Properties().stacksTo(1)));

    // Essence Shards
    public static final Supplier<Item> ESSENCE_SHARD = register("essence_shard", () -> new EssenceShard(new Item.Properties(), Map.of(EssenceTypeRegistry.ESSENCE, 100f)));

    // Wires
    public static final Supplier<Item> ESSENCE_WIRE = register("essence_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> LUNAR_ESSENCE_WIRE = register("lunar_essence_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> NATURAL_ESSENCE_WIRE = register("natural_essence_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> EXOTIC_ESSENCE_WIRE = register("exotic_essence_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ITEM_WIRE = register("item_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> FLUID_WIRE = register("fluid_wire", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> DATA_TABLET = register("data_tablet", () -> new DataTablet(new Item.Properties().stacksTo(1)));

    // BlockItems
    public static final Supplier<Item> FABRICATOR_ITEM = register("fabricator", () -> new FabricatorItem(BlockRegistry.FABRICATOR.get(), new Item.Properties()));
    public static final Supplier<Item> ESSENCE_POINT_ITEM = register("essence_point", () -> new EssencePointItem(BlockRegistry.ESSENCE_POINT.get(), new Item.Properties()));
    public static final Supplier<Item> LUNAR_ESSENCE_POINT_ITEM = register("lunar_essence_point", () -> new LunarEssencePointItem(BlockRegistry.LUNAR_ESSENCE_POINT.get(), new Item.Properties()));
    public static final Supplier<Item> NATURAL_ESSENCE_POINT_ITEM = register("natural_essence_point", () -> new NaturalEssencePointItem(BlockRegistry.NATURAL_ESSENCE_POINT.get(), new Item.Properties()));
    public static final Supplier<Item> EXOTIC_ESSENCE_POINT_ITEM = register("exotic_essence_point", () -> new ExoticEssencePointItem(BlockRegistry.EXOTIC_ESSENCE_POINT.get(), new Item.Properties()));
    public static final Supplier<Item> ITEM_POINT_ITEM = register("item_point", () -> new ItemPointItem(BlockRegistry.ITEM_POINT.get(), new Item.Properties()));
    public static final Supplier<Item> FLUID_POINT_ITEM = register("fluid_point", () -> new FluidPointItem(BlockRegistry.FLUID_POINT.get(), new Item.Properties()));
    public static final Supplier<Item> INFUSER_ITEM = register("infuser", () -> new InfuserItem(BlockRegistry.INFUSER.get(), new Item.Properties()));
    public static final Supplier<Item> CHARGER_ITEM = register("charger", () -> new ChargerItem(BlockRegistry.CHARGER.get(), new Item.Properties()));
    public static final Supplier<Item> AUTO_FABRICATOR_ITEM = register("auto-fabricator", () -> new AutoFabricatorItem(BlockRegistry.AUTO_FABRICATOR.get(), new Item.Properties()));
    public static final Supplier<Item> ENTROPIC_PROCESSOR_ITEM = register("entropic_processor", () -> new EntropicProcessorItem(BlockRegistry.ENTROPIC_PROCESSOR.get(), new Item.Properties()));

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

    // Ore Processing Intermediates and Nuggets
    public static final Supplier<Item> COPPER_NUGGET = register("copper_nugget", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> DIAMOND_SHARD = register("diamond_shard", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> EMERALD_SHARD = register("emerald_shard", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> LAPIS_SHARD = register("lapis_shard", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> COAL_LUMP = register("coal_lump", () -> new Item(new Item.Properties()));

    // Misc
    public static final Supplier<Item> COGNIZANT_CUBE = register("cognizant_cube", () -> new CognizantCube(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> MUSIC_DISC_PLAYER = register("music_disc_player", () -> new MusicDiscPlayer(new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)));
    public static final Supplier<Item> TRAVERSITE_ROAD_CHUNK = register("traversite_road_chunk", () -> new TraversiteRoadChunk(new Item.Properties()));

    // Node Upgrades
    public static final Supplier<Item> SPEED_UPGRADE = register("speed_upgrade", () -> new SpeedNodeUpgrade(new Item.Properties(), 1));
    public static final Supplier<Item> FILTER_UPGRADE = register("filter_upgrade", () -> new FilterNodeUpgrade(new Item.Properties()));
    public static final Supplier<Item> LIMITER_UPGRADE = register("limiter_upgrade", () -> new LimiterNodeUpgrade(new Item.Properties()));

    private static <T extends Item> Supplier<T> register(final String name, final Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
