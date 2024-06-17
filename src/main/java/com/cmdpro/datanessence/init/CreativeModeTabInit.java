package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CreativeModeTabInit {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            DataNEssence.MOD_ID);

    public static RegistryObject<CreativeModeTab> ITEMS = register("datanessence_items", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.DATA_TABLET.get()))
                    .title(Component.translatable("creativemodetab.datanessence_items")).build());
    public static RegistryObject<CreativeModeTab> BLOCKS = CREATIVE_MODE_TABS.register("datanessence_blocks", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.FABRICATOR_ITEM.get()))
                    .title(Component.translatable("creativemodetab.datanessence_blocks")).build());

    private static <T extends CreativeModeTab> RegistryObject<T> register(final String name, final Supplier<T> tab) {
        return CREATIVE_MODE_TABS.register(name, tab);
    }
}
