package com.cmdpro.datanessence.data.datatablet;

import com.cmdpro.datanessence.api.datatablet.Page;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class Entry {

    public Entry(ResourceLocation id, ResourceLocation tab, ItemLike icon, int x, int y, List<Page> pages, List<ResourceLocation> parents, Component name, Component flavor, boolean critical, List<CompletionStage> completionStages, boolean isDefault) {
        this.id = id;
        this.icon = new ItemStack(icon);
        this.x = x;
        this.y = y;
        this.pages = pages;
        this.parents = parents;
        this.name = name;
        this.flavor = flavor;
        this.critical = critical;
        this.tab = tab;
        this.completionStages = completionStages;
        this.isDefault = isDefault;
    }
    public Entry(ResourceLocation id, ResourceLocation tab, ItemStack icon, int x, int y, List<Page> pages, List<ResourceLocation> parents, Component name, Component flavor, boolean critical, List<CompletionStage> completionStages, boolean isDefault) {
        this.id = id;
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.pages = pages;
        this.parents = parents;
        this.name = name;
        this.flavor = flavor;
        this.critical = critical;
        this.tab = tab;
        this.completionStages = completionStages;
        this.isDefault = isDefault;
    }
    public ItemStack getIcon(int stage) {
        return isIncomplete(stage) ? completionStages.get(stage).iconOverride.orElse(icon) : icon;
    }
    public Component getName(int stage) {
        return isIncomplete(stage) ? completionStages.get(stage).nameOverride.orElse(name) : name;
    }
    public Component getFlavor(int stage) {
        return isIncomplete(stage) ? completionStages.get(stage).flavorOverride.orElse(flavor) : flavor;
    }
    public List<Page> getPagesClient() {
        return isIncompleteClient() ? completionStages.get(getIncompleteStageClient()).pages : pages;
    }
    public int getIncompleteStageClient() {
        return ClientPlayerUnlockedEntries.getIncomplete().getOrDefault(id, ClientPlayerUnlockedEntries.getUnlocked().contains(id) ? completionStages.size() : 0);
    }
    public boolean isIncompleteClient() {
        return isIncomplete(getIncompleteStageClient());
    }
    public boolean isVisibleClient() {
        return isUnlockedClient() && (ClientPlayerUnlockedEntries.getUnlocked().contains(id) || ClientPlayerUnlockedEntries.getIncomplete().containsKey(id));
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
        List<ResourceLocation> unlockedEntries = player.getData(AttachmentTypeRegistry.UNLOCKED);
        boolean unlocked = true;
        for (Entry i : getParentEntries()) {
            if (!unlockedEntries.contains(i.id)) {
                unlocked = false;
                break;
            }
        }
        return unlocked;
    }
    public int getIncompleteStageServer(Player player) {
        return player.getData(AttachmentTypeRegistry.INCOMPLETE_STAGES).getOrDefault(id, player.getData(AttachmentTypeRegistry.UNLOCKED).contains(id) ? completionStages.size() : -1);
    }
    public boolean isIncompleteServer(Player player) {
        return isIncomplete(getIncompleteStageServer(player));
    }
    public boolean isIncomplete(int stage) {
        return stage < completionStages.size();
    }
    public ResourceLocation tab;
    public List<Entry> getParentEntries() {
        return parentEntries;
    }
    public void setParentEntries(List<ResourceLocation> entry) {
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
        this.parentEntries = parentEntries;
        return !parentEntries.isEmpty();
    }
    public boolean critical;
    public Component name;
    public Component flavor;
    public ItemStack icon;
    public ResourceLocation id;
    public int x;
    public int y;
    public List<Page> pages;
    public List<ResourceLocation> parents;
    private List<Entry> parentEntries;
    public List<CompletionStage> completionStages;
    public boolean isDefault;
}
