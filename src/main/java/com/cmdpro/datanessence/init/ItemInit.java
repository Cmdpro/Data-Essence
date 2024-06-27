package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.*;
import com.cmdpro.datanessence.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DataNEssence.MOD_ID);

    // Tools
    public static final RegistryObject<Item> MAGIC_WRENCH = register("magic_wrench", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DATA_DRIVE = register("data_drive", () -> new DataDrive(new Item.Properties()));

    // Essence Shards
    public static final RegistryObject<Item> ESSENCE_SHARD = register("essence_shard", () -> new EssenceShard(new Item.Properties(), 100, 0, 0, 0));

    // Wires
    public static final RegistryObject<Item> ESSENCE_WIRE = register("essence_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LUNAR_ESSENCE_WIRE = register("lunar_essence_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NATURAL_ESSENCE_WIRE = register("natural_essence_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EXOTIC_ESSENCE_WIRE = register("exotic_essence_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ITEM_WIRE = register("item_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FLUID_WIRE = register("fluid_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DATA_TABLET = register("data_tablet", () -> new DataTablet(new Item.Properties().stacksTo(1)));

    // BlockItems
    public static final RegistryObject<Item> FABRICATOR_ITEM = register("fabricator", () -> new FabricatorItem(BlockInit.FABRICATOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ESSENCE_POINT_ITEM = register("essence_point", () -> new EssencePointItem(BlockInit.ESSENCE_POINT.get(), new Item.Properties()));
    public static final RegistryObject<Item> LUNAR_ESSENCE_POINT_ITEM = register("lunar_essence_point", () -> new LunarEssencePointItem(BlockInit.LUNAR_ESSENCE_POINT.get(), new Item.Properties()));
    public static final RegistryObject<Item> NATURAL_ESSENCE_POINT_ITEM = register("natural_essence_point", () -> new NaturalEssencePointItem(BlockInit.NATURAL_ESSENCE_POINT.get(), new Item.Properties()));
    public static final RegistryObject<Item> EXOTIC_ESSENCE_POINT_ITEM = register("exotic_essence_point", () -> new ExoticEssencePointItem(BlockInit.EXOTIC_ESSENCE_POINT.get(), new Item.Properties()));
    public static final RegistryObject<Item> ITEM_POINT_ITEM = register("item_point", () -> new ItemPointItem(BlockInit.ITEM_POINT.get(), new Item.Properties()));
    public static final RegistryObject<Item> FLUID_POINT_ITEM = register("fluid_point", () -> new FluidPointItem(BlockInit.FLUID_POINT.get(), new Item.Properties()));
    public static final RegistryObject<Item> INFUSER_ITEM = register("infuser", () -> new InfuserItem(BlockInit.INFUSER.get(), new Item.Properties()));

    // Bombs
    public static final RegistryObject<Item> ESSENCE_BOMB = register("essence_bomb", () -> new EssenceBombItem(new Item.Properties()));
    public static final RegistryObject<Item> LUNAR_ESSENCE_BOMB = register("lunar_essence_bomb", () -> new LunarEssenceBombItem(new Item.Properties()));
    public static final RegistryObject<Item> NATURAL_ESSENCE_BOMB = register("natural_essence_bomb", () -> new NaturalEssenceBombItem(new Item.Properties()));
    public static final RegistryObject<Item> EXOTIC_ESSENCE_BOMB = register("exotic_essence_bomb", () -> new ExoticEssenceBombItem(new Item.Properties()));

    // Crafting Components
    public static final RegistryObject<Item> CONDUCTANCE_ROD = register("conductance_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CAPACITANCE_PANEL = register("capacitance_panel", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LOGICAL_MATRIX = register("logical_matrix", () -> new Item(new Item.Properties()));

    private static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
