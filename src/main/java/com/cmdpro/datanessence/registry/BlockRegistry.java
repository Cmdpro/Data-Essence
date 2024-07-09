package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK,
            DataNEssence.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = ItemRegistry.ITEMS;

    // World
    public static final Supplier<Block> ESSENCE_CRYSTAL = register("essence_crystal",
            () -> new EssenceCrystal(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Generators
    public static final Supplier<Block> ESSENCE_BURNER = register("essence_burner",
            () -> new EssenceBurner(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Machines
    public static final Supplier<Block> FABRICATOR = registerBlock("fabricator",
            () -> new Fabricator(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final Supplier<Block> INFUSER = registerBlock("infuser",
            () -> new Infuser(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));

    // Buffers
    public static final Supplier<Block> ESSENCE_BUFFER = register("essence_buffer",
            () -> new EssenceBuffer(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ITEM_BUFFER = register("item_buffer",
            () -> new ItemBuffer(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> FLUID_BUFFER = register("fluid_buffer",
            () -> new FluidBuffer(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> DECO_ESSENCE_BUFFER = register("deco_essence_buffer",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> DECO_FLUID_BUFFER = register("deco_fluid_buffer",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> DECO_ITEM_BUFFER = register("deco_item_buffer",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Points
    public static final Supplier<Block> ESSENCE_POINT = registerBlock("essence_point",
            () -> new EssencePoint(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final Supplier<Block> LUNAR_ESSENCE_POINT = registerBlock("lunar_essence_point",
            () -> new LunarEssencePoint(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final Supplier<Block> NATURAL_ESSENCE_POINT = registerBlock("natural_essence_point",
            () -> new NaturalEssencePoint(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final Supplier<Block> EXOTIC_ESSENCE_POINT = registerBlock("exotic_essence_point",
            () -> new ExoticEssencePoint(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final Supplier<Block> FLUID_POINT = registerBlock("fluid_point",
            () -> new FluidPoint(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final Supplier<Block> ITEM_POINT = registerBlock("item_point",
            () -> new ItemPoint(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));

    // Batteries
    public static final Supplier<Block> ESSENCE_BATTERY = register("essence_battery",
            () -> new EssenceBattery(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> LUNAR_ESSENCE_BATTERY = register("lunar_essence_battery",
            () -> new LunarEssenceBattery(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> NATURAL_ESSENCE_BATTERY = register("natural_essence_battery",
            () -> new NaturalEssenceBattery(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> EXOTIC_ESSENCE_BATTERY = register("exotic_essence_battery",
            () -> new ExoticEssenceBattery(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // these need categorization.
    public static final Supplier<Block> DATA_BANK = register("data_bank",
            () -> new DataBank(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f).lightLevel((blockState) -> { return 4;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> COMPUTER = register("computer",
            () -> new Computer(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f).lightLevel((blockState) -> { return 4;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> TRAVERSITE_ROAD = register("traversite_road",
            () -> new TraversiteRoad(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion().strength(2.0f), 2),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Ancient Rock
    public static final Supplier<Block> ANCIENT_ROCK_BRICKS = register("ancient_rock_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_ROCK_TILES = register("ancient_rock_tiles",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_ROCK_COLUMN = register("ancient_rock_column",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ENERGIZED_ANCIENT_ROCK_COLUMN = register("energized_ancient_rock_column",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel((blockState) -> { return 3;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ANCIENT_LANTERN = register("ancient_lantern",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel((blockState) -> { return 13;})),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Obsidian Deco
    public static final Supplier<Block> POLISHED_OBSIDIAN = register("polished_obsidian",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> POLISHED_OBSIDIAN_COLUMN = register("polished_obsidian_column",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final Supplier<Block> ENGRAVED_POLISHED_OBSIDIAN = register("engraved_polished_obsidian",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Copper Deco
    public static final Supplier<Block> PATTERNED_COPPER = register("patterned_copper",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

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
    private static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType<?> entityType) {
        return false;
    }
    private static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }
}
