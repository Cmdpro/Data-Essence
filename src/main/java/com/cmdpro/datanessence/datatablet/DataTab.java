package com.cmdpro.datanessence.datatablet;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class DataTab {

    public DataTab(ResourceLocation id, ItemLike icon, Component name, int priority) {
        this.id = id;
        this.icon = new ItemStack(icon);
        this.name = name;
        this.priority = priority;
    }
    public DataTab(ResourceLocation id, ItemStack icon, Component name, int priority) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.priority = priority;
    }
    public Component name;
    public ItemStack icon;
    public ResourceLocation id;
    public int priority;
}
