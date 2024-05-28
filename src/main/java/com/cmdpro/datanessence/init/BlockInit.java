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
    public static final RegistryObject<Block> ESSENCEBUFFER = register("essencebuffer",
            () -> new EssenceBuffer(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> ITEMBUFFER = register("itembuffer",
            () -> new ItemBuffer(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> FLUIDBUFFER = register("fluidbuffer",
            () -> new FluidBuffer(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> ESSENCEPOINT = registerBlock("essencepoint",
            () -> new EssencePoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> LUNARESSENCEPOINT = registerBlock("lunaressencepoint",
            () -> new LunarEssencePoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> NATURALESSENCEPOINT = registerBlock("naturalessencepoint",
            () -> new NaturalEssencePoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> EXOTICESSENCEPOINT = registerBlock("exoticessencepoint",
            () -> new ExoticEssencePoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> FLUIDPOINT = registerBlock("fluidpoint",
            () -> new FluidPoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> ITEMPOINT = registerBlock("itempoint",
            () -> new ItemPoint(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)));
    public static final RegistryObject<Block> DATABANK = register("databank",
            () -> new DataBank(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().strength(2.0f)),
            object -> () -> new BlockItem(object.get(), new Item.Properties()));
    public static final RegistryObject<Block> ESSENCECRYSTAL = register("essencecrystal",
            () -> new EssenceCrystal(BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER).noOcclusion().strength(2.0f)),
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
