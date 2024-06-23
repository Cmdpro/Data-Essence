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
    public static final RegistryObject<BlockEntityType<EssenceBurnerBlockEntity>> ESSENCE_BURNER =
            register("essence_burner", () ->
                    BlockEntityType.Builder.of(EssenceBurnerBlockEntity::new,
                            BlockInit.ESSENCE_BURNER.get()).build(null));
    public static final RegistryObject<BlockEntityType<EssencePointBlockEntity>> ESSENCE_POINT =
            register("essence_point", () ->
                    BlockEntityType.Builder.of(EssencePointBlockEntity::new,
                            BlockInit.ESSENCE_POINT.get()).build(null));
    public static final RegistryObject<BlockEntityType<LunarEssencePointBlockEntity>> LUNAR_ESSENCE_POINT =
            register("lunar_essence_point", () ->
                    BlockEntityType.Builder.of(LunarEssencePointBlockEntity::new,
                            BlockInit.LUNAR_ESSENCE_POINT.get()).build(null));
    public static final RegistryObject<BlockEntityType<NaturalEssencePointBlockEntity>> NATURAL_ESSENCE_POINT =
            register("natural_essence_point", () ->
                    BlockEntityType.Builder.of(NaturalEssencePointBlockEntity::new,
                            BlockInit.NATURAL_ESSENCE_POINT.get()).build(null));
    public static final RegistryObject<BlockEntityType<ExoticEssencePointBlockEntity>> EXOTIC_ESSENCE_POINT =
            register("exotic_essence_point", () ->
                    BlockEntityType.Builder.of(ExoticEssencePointBlockEntity::new,
                            BlockInit.EXOTIC_ESSENCE_POINT.get()).build(null));
    public static final RegistryObject<BlockEntityType<ItemPointBlockEntity>> ITEM_POINT =
            register("item_point", () ->
                    BlockEntityType.Builder.of(ItemPointBlockEntity::new,
                            BlockInit.ITEM_POINT.get()).build(null));
    public static final RegistryObject<BlockEntityType<FluidPointBlockEntity>> FLUID_POINT =
            register("fluid_point", () ->
                    BlockEntityType.Builder.of(FluidPointBlockEntity::new,
                            BlockInit.FLUID_POINT.get()).build(null));
    public static final RegistryObject<BlockEntityType<EssenceBufferBlockEntity>> ESSENCE_BUFFER =
            register("essence_buffer", () ->
                    BlockEntityType.Builder.of(EssenceBufferBlockEntity::new,
                            BlockInit.ESSENCE_BUFFER.get()).build(null));
    public static final RegistryObject<BlockEntityType<ItemBufferBlockEntity>> ITEM_BUFFER =
            register("item_buffer", () ->
                    BlockEntityType.Builder.of(ItemBufferBlockEntity::new,
                            BlockInit.ITEM_BUFFER.get()).build(null));
    public static final RegistryObject<BlockEntityType<FluidBufferBlockEntity>> FLUID_BUFFER =
            register("fluid_buffer", () ->
                    BlockEntityType.Builder.of(FluidBufferBlockEntity::new,
                            BlockInit.FLUID_BUFFER.get()).build(null));
    public static final RegistryObject<BlockEntityType<DataBankBlockEntity>> DATA_BANK =
            register("data_bank", () ->
                    BlockEntityType.Builder.of(DataBankBlockEntity::new,
                            BlockInit.DATA_BANK.get()).build(null));
    public static final RegistryObject<BlockEntityType<EssenceBatteryBlockEntity>> ESSENCE_BATTERY =
            register("essence_battery", () ->
                    BlockEntityType.Builder.of(EssenceBatteryBlockEntity::new,
                            BlockInit.ESSENCE_BATTERY.get()).build(null));
    public static final RegistryObject<BlockEntityType<LunarEssenceBatteryBlockEntity>> LUNAR_ESSENCE_BATTERY =
            register("lunar_essence_battery", () ->
                    BlockEntityType.Builder.of(LunarEssenceBatteryBlockEntity::new,
                            BlockInit.LUNAR_ESSENCE_BATTERY.get()).build(null));
    public static final RegistryObject<BlockEntityType<NaturalEssenceBatteryBlockEntity>> NATURAL_ESSENCE_BATTERY =
            register("natural_essence_battery", () ->
                    BlockEntityType.Builder.of(NaturalEssenceBatteryBlockEntity::new,
                            BlockInit.NATURAL_ESSENCE_BATTERY.get()).build(null));
    public static final RegistryObject<BlockEntityType<ExoticEssenceBatteryBlockEntity>> EXOTIC_ESSENCE_BATTERY =
            register("exotic_essence_battery", () ->
                    BlockEntityType.Builder.of(ExoticEssenceBatteryBlockEntity::new,
                            BlockInit.EXOTIC_ESSENCE_BATTERY.get()).build(null));


    private static <T extends BlockEntityType<?>> RegistryObject<T> register(final String name, final Supplier<T> blockentity) {
        return BLOCK_ENTITIES.register(name, blockentity);
    }
}
