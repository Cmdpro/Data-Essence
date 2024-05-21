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
    public static final RegistryObject<Item> ESSENCEWIRE = register("essencewire", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DATATABLET = register("datatablet", () -> new DataTablet(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FABRICATORITEM = register("fabricator", () -> new FabricatorItem(BlockInit.FABRICATOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ESSENCEPOINTITEM = register("essencepoint", () -> new EssencePointItem(BlockInit.ESSENCEPOINT.get(), new Item.Properties()));
    private static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
