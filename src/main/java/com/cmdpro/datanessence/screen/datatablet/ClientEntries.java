package com.cmdpro.datanessence.screen.datatablet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.init.ItemInit;
import com.cmdpro.datanessence.screen.datatablet.pages.CraftingPage;
import com.cmdpro.datanessence.screen.datatablet.pages.TextPage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class ClientEntries {
    public static HashMap<ResourceLocation, Entry> entries = new HashMap<>();
    static {
        entries.put(new ResourceLocation(DataNEssence.MOD_ID, "test1"), new Entry(new ResourceLocation(DataNEssence.MOD_ID, "test1"), ItemInit.DATATABLET.get(), 0, 0, new Page[] {
                new TextPage(Component.literal("This page is cool")),
                new TextPage(Component.literal("Page 2 works!")),
                new TextPage(Component.literal("Page 3 works as well!"))

        }, null));
        entries.put(new ResourceLocation(DataNEssence.MOD_ID, "test2"), new Entry(new ResourceLocation(DataNEssence.MOD_ID, "test2"), ItemInit.DATATABLET.get(), -3, 2, new Page[] {
                new CraftingPage(Component.literal("testing"), new ResourceLocation[] { new ResourceLocation(DataNEssence.MOD_ID, "testrecipe"), new ResourceLocation("minecraft", "ender_eye"), new ResourceLocation("minecraft", "crafting_table") })
        }, new ResourceLocation(DataNEssence.MOD_ID, "test1")));
        entries.put(new ResourceLocation(DataNEssence.MOD_ID, "test3"), new Entry(new ResourceLocation(DataNEssence.MOD_ID, "test3"), ItemInit.DATATABLET.get(), -1, 2, new Page[] {}, new ResourceLocation(DataNEssence.MOD_ID, "test1")));
        entries.put(new ResourceLocation(DataNEssence.MOD_ID, "test4"), new Entry(new ResourceLocation(DataNEssence.MOD_ID, "test4"), ItemInit.DATATABLET.get(), 1, 2, new Page[] {}, new ResourceLocation(DataNEssence.MOD_ID, "test1")));
        entries.put(new ResourceLocation(DataNEssence.MOD_ID, "test5"), new Entry(new ResourceLocation(DataNEssence.MOD_ID, "test5"), ItemInit.DATATABLET.get(), 3, 2, new Page[] {}, new ResourceLocation(DataNEssence.MOD_ID, "test1")));
        for (Entry i : entries.values()) {
            i.updateParentEntry();
        }
    }
}
