package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeModeTabInit {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            DataNEssence.MOD_ID);

    public static RegistryObject<CreativeModeTab> ITEMS = CREATIVE_MODE_TABS.register("datanessenceitems", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.DATATABLET.get()))
                    .title(Component.translatable("creativemodetab.datanessenceitems")).build());
    //public static RegistryObject<CreativeModeTab> BLOCKS = CREATIVE_MODE_TABS.register("datanessenceblocks", () ->
    //        CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.RUNICWORKBENCHITEM.get()))
    //                .title(Component.translatable("creativemodetab.datanessenceblocks")).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
