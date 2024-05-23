package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.entity.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DataNEssence.MOD_ID);
    public static final RegistryObject<BlockEntityType<FabricatorBlockEntity>> FABRICATOR =
            register("fabricator", () ->
                    BlockEntityType.Builder.of(FabricatorBlockEntity::new,
                            BlockInit.FABRICATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<EssencePointBlockEntity>> ESSENCEPOINT =
            register("essencepoint", () ->
                    BlockEntityType.Builder.of(EssencePointBlockEntity::new,
                            BlockInit.ESSENCEPOINT.get()).build(null));
    public static final RegistryObject<BlockEntityType<EssenceBufferBlockEntity>> ESSENCEBUFFER =
            register("essencebuffer", () ->
                    BlockEntityType.Builder.of(EssenceBufferBlockEntity::new,
                            BlockInit.ESSENCEBUFFER.get()).build(null));
    public static final RegistryObject<BlockEntityType<ItemBufferBlockEntity>> ITEMBUFFER =
            register("itembuffer", () ->
                    BlockEntityType.Builder.of(ItemBufferBlockEntity::new,
                            BlockInit.ITEMBUFFER.get()).build(null));
    public static final RegistryObject<BlockEntityType<FluidBufferBlockEntity>> FLUIDBUFFER =
            register("fluidbuffer", () ->
                    BlockEntityType.Builder.of(FluidBufferBlockEntity::new,
                            BlockInit.FLUIDBUFFER.get()).build(null));


    private static <T extends BlockEntityType<?>> RegistryObject<T> register(final String name, final Supplier<T> blockentity) {
        return BLOCK_ENTITIES.register(name, blockentity);
    }
}
