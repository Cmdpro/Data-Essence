package com.cmdpro.datanessence.databank;

import com.cmdpro.datanessence.api.databank.MinigameCreator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class DataBankEntry {

    public DataBankEntry(ResourceLocation id, ItemLike icon, int tier, MinigameCreator[] minigames, Component name, ResourceLocation entry) {
        this.id = id;
        this.icon = new ItemStack(icon);
        this.tier = tier;
        this.minigames = minigames;
        this.name = name;
        this.entry = entry;
    }
    public DataBankEntry(ResourceLocation id, ItemStack icon, int tier, MinigameCreator[] minigames, Component name, ResourceLocation entry) {
        this.id = id;
        this.icon = icon;
        this.tier = tier;
        this.minigames = minigames;
        this.name = name;
        this.entry = entry;
    }
    public Component name;
    public ItemStack icon;
    public ResourceLocation id;
    public int tier;
    public ResourceLocation entry;
    public MinigameCreator[] minigames;
}
