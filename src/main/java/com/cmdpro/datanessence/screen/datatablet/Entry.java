package com.cmdpro.datanessence.screen.datatablet;

import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.moddata.PlayerModData;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class Entry {

    public Entry(ResourceLocation id, ResourceLocation tab, ItemLike icon, int x, int y, Page[] pages, ResourceLocation[] parents, Component name, boolean critical) {
        this.id = id;
        this.icon = new ItemStack(icon);
        this.x = x;
        this.y = y;
        this.pages = pages;
        this.parents = parents;
        this.name = name;
        this.critical = critical;
        this.tab = tab;
    }
    public Entry(ResourceLocation id, ResourceLocation tab, ItemStack icon, int x, int y, Page[] pages, ResourceLocation[] parents, Component name, boolean critical) {
        this.id = id;
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.pages = pages;
        this.parents = parents;
        this.name = name;
        this.critical = critical;
        this.tab = tab;
    }
    public boolean isUnlockedClient() {
        boolean unlocked = true;
        for (Entry i : getParentEntries()) {
            if (!ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                unlocked = false;
                break;
            }
        }
        return unlocked;
    }
    public boolean isUnlockedServer(Player player) {
        if (!player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).isPresent()) {
            return false;
        }
        PlayerModData data = player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).resolve().get();
        boolean unlocked = true;
        for (Entry i : getParentEntries()) {
            if (!data.getUnlocked().contains(i.id)) {
                unlocked = false;
                break;
            }
        }
        return unlocked;
    }
    public ResourceLocation tab;
    public Entry[] getParentEntries() {
        return parentEntries;
    }
    public void setParentEntries(ResourceLocation[] entry) {
        this.parents = entry;
        updateParentEntries();
    }
    public boolean updateParentEntries() {
        List<Entry> parentEntries = new ArrayList<>();
        for (ResourceLocation i : parents) {
            if (Entries.entries.containsKey(i)) {
                parentEntries.add(Entries.entries.get(i));
            }
        }
        this.parentEntries = parentEntries.toArray(new Entry[0]);
        return !parentEntries.isEmpty();
    }
    public boolean critical;
    public Component name;
    public ItemStack icon;
    public ResourceLocation id;
    public int x;
    public int y;
    public Page[] pages;
    private ResourceLocation[] parents;
    private Entry[] parentEntries;
}
