package com.cmdpro.datanessence.screen.datatablet;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.CraftingHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataTabSerializer {
    public DataTab read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("icon")) {
            throw new JsonSyntaxException("Element color missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("name")) {
            throw new JsonSyntaxException("Element name missing in entry JSON for " + entryId.toString());
        }
        ItemStack icon = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(GsonHelper.getAsString(json, "icon"))));
        Component name = Component.translatable(json.get("name").getAsString());
        int priority = 0;
        if (json.has("priority")) {
            priority = json.get("priority").getAsInt();
        }
        DataTab entry = new DataTab(entryId, icon, name, priority);
        return entry;
    }
    @Nonnull
    public static DataTab fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        ItemStack icon = buf.readItem();
        Component name = buf.readComponent();
        int priority = buf.readInt();
        DataTab entry = new DataTab(id, icon, name, priority);
        return entry;
    }
    public static void toNetwork(FriendlyByteBuf buf, DataTab entry) {
        buf.writeResourceLocation(entry.id);
        buf.writeItem(entry.icon);
        buf.writeComponent(entry.name);
        buf.writeInt(entry.priority);
    }
}