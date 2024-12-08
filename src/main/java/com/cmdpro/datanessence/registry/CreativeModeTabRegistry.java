package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CreativeModeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB,
            DataNEssence.MOD_ID);

    public static Supplier<CreativeModeTab> ITEMS = register("datanessence_items", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ItemRegistry.DATA_TABLET.get()))
                    .title(Component.translatable("creativemodetab.datanessence_items")).build());
    public static Supplier<CreativeModeTab> BLOCKS = register("datanessence_blocks", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(BlockRegistry.FABRICATOR.get()))
                    .title(Component.translatable("creativemodetab.datanessence_blocks")).build());
    public static ResourceKey getKey(CreativeModeTab tab) {
        return BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).get();
    }
    private static <T extends CreativeModeTab> Supplier<T> register(final String name, final Supplier<T> tab) {
        return CREATIVE_MODE_TABS.register(name, tab);
    }
}
