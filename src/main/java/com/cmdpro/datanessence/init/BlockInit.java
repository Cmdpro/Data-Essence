package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            DataNEssence.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = ItemInit.ITEMS;
    public static final RegistryObject<Block> FABRICATOR = registerBlock("fabricator",
            () -> new Fabricator(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> ESSENCE_BUFFER = register("essence_buffer",
            () -> new EssenceBuffer(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> ITEM_BUFFER = register("item_buffer",
            () -> new ItemBuffer(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> FLUID_BUFFER = register("fluid_buffer",
            () -> new FluidBuffer(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> ESSENCE_POINT = registerBlock("essence_point",
            () -> new EssencePoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> LUNAR_ESSENCE_POINT = registerBlock("lunar_essence_point",
            () -> new LunarEssencePoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> NATURAL_ESSENCE_POINT = registerBlock("natural_essence_point",
            () -> new NaturalEssencePoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> EXOTIC_ESSENCE_POINT = registerBlock("exotic_essence_point",
            () -> new ExoticEssencePoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> FLUID_POINT = registerBlock("fluid_point",
            () -> new FluidPoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> ITEM_POINT = registerBlock("item_point",
            () -> new ItemPoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> DATA_BANK = register("data_bank",
            () -> new DataBank(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> ESSENCE_BURNER = register("essence_burner",
            () -> new EssenceBurner(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> ESSENCE_CRYSTAL = register("essence_crystal",
            () -> new EssenceCrystal(BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> DECO_ESSENCE_BUFFER = register("deco_essence_buffer",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> DECO_FLUID_BUFFER = register("deco_fluid_buffer",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> DECO_ITEM_BUFFER = register("deco_item_buffer",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Ancient Rock
    public static final RegistryObject<Block> ANCIENT_ROCK_BRICKS = register("ancient_rock_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> ANCIENT_ROCK_TILES = register("ancient_rock_tiles",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> ANCIENT_ROCK_COLUMN = register("ancient_rock_column",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> ENERGIZED_ANCIENT_ROCK_COLUMN = register("energized_ancient_rock_column",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    // Obsidian Deco
    public static final RegistryObject<Block> POLISHED_OBSIDIAN = register("polished_obsidian",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));

    private static <T extends Block> RegistryObject<T> registerBlock(final String name,
                                                                     final Supplier<? extends T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(final String name, final Supplier<? extends T> block,
                                                                Function<RegistryObject<T>, Supplier<? extends Item>> item) {
        RegistryObject<T> obj = registerBlock(name, block);
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
