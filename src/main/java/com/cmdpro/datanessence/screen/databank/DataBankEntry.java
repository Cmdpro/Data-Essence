package com.cmdpro.datanessence.screen.databank;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class DataBankEntry {

    public DataBankEntry(ResourceLocation id, ItemLike icon, int tier, MinigameCreator[] minigames, Component name) {
        this.id = id;
        this.icon = new ItemStack(icon);
        this.tier = tier;
        this.minigames = minigames;
        this.name = name;
    }
    public DataBankEntry(ResourceLocation id, ItemStack icon, int tier, MinigameCreator[] minigames, Component name) {
        this.id = id;
        this.icon = icon;
        this.tier = tier;
        this.minigames = minigames;
        this.name = name;
    }
    public Component name;
    public ItemStack icon;
    public ResourceLocation id;
    public int tier;
    public MinigameCreator[] minigames;
}
