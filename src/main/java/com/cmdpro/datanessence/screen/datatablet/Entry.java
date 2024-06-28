package com.cmdpro.datanessence.screen.datatablet;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class Entry {

    public Entry(ResourceLocation id, ResourceLocation tab, ItemLike icon, int x, int y, Page[] pages, ResourceLocation parent, Component name, boolean critical) {
        this.id = id;
        this.icon = new ItemStack(icon);
        this.x = x;
        this.y = y;
        this.pages = pages;
        this.parent = parent;
        this.name = name;
        this.critical = critical;
        this.tab = tab;
    }
    public Entry(ResourceLocation id, ResourceLocation tab, ItemStack icon, int x, int y, Page[] pages, ResourceLocation parent, Component name, boolean critical) {
        this.id = id;
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.pages = pages;
        this.parent = parent;
        this.name = name;
        this.critical = critical;
        this.tab = tab;
    }
    public ResourceLocation tab;
    public Entry getParentEntry() {
        return parentEntry;
    }
    public void setParentEntry(ResourceLocation entry) {
        this.parent = entry;
        updateParentEntry();
    }
    public boolean updateParentEntry() {
        if (Entries.entries.containsKey(parent)) {
            this.parentEntry = Entries.entries.get(parent);
            return true;
        } else {
            return false;
        }
    }
    public boolean critical;
    public Component name;
    public ItemStack icon;
    public ResourceLocation id;
    public int x;
    public int y;
    public Page[] pages;
    private ResourceLocation parent;
    private Entry parentEntry;
}
