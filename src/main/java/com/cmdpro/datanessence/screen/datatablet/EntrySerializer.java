package com.cmdpro.datanessence.screen.datatablet;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntrySerializer {
    public Entry read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("icon")) {
            throw new JsonSyntaxException("Element icon missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("x")) {
            throw new JsonSyntaxException("Element x missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("y")) {
            throw new JsonSyntaxException("Element y missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("name")) {
            throw new JsonSyntaxException("Element name missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("pages")) {
            throw new JsonSyntaxException("Element pages missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("tab")) {
            throw new JsonSyntaxException("Element tab missing in entry JSON for " + entryId.toString());
        }
        ItemStack icon = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(GsonHelper.getAsString(json, "icon"))));
        int x = json.get("x").getAsInt();
        int y = json.get("y").getAsInt();
        Component name = Component.translatable(json.get("name").getAsString());
        List<ResourceLocation> parents = new ArrayList<>();
        if (json.has("parents")) {
            for (JsonElement i : json.getAsJsonArray("parents")) {
                parents.add(ResourceLocation.tryParse(i.getAsString()));
            }
        }
        List<Page> pages = new ArrayList<>();
        int o = 0;
        for (JsonElement i : json.getAsJsonArray("pages")) {
            JsonObject obj = i.getAsJsonObject();
            if (obj.has("type")) {
                PageSerializer pageSerializer = DataNEssenceRegistries.PAGE_TYPE_REGISTRY.get(ResourceLocation.tryParse(obj.get("type").getAsString()));
                Page page = pageSerializer.fromJson(obj);
                pages.add(page);
            } else {
                throw new JsonSyntaxException("Page type missing in entry JSON for " + entryId.toString() + " in page " + o);
            }
            o++;
        }
        boolean critical = false;
        if (json.has("critical")) {
            critical = json.get("critical").getAsBoolean();
        }
        ResourceLocation tab = ResourceLocation.tryParse(json.get("tab").getAsString());
        boolean incomplete = false;
        ResourceLocation completionAdvancement = ResourceLocation.fromNamespaceAndPath("", "");
        List<Page> incompletePages = new ArrayList<>();
        if (json.has("incomplete")) {
            incomplete = json.get("incomplete").getAsBoolean();
            int p = 0;
            for (JsonElement i : json.getAsJsonArray("incompletePages")) {
                JsonObject obj = i.getAsJsonObject();
                if (obj.has("type")) {
                    PageSerializer pageSerializer = DataNEssenceRegistries.PAGE_TYPE_REGISTRY.get(ResourceLocation.tryParse(obj.get("type").getAsString()));
                    Page page = pageSerializer.fromJson(obj);
                    incompletePages.add(page);
                } else {
                    throw new JsonSyntaxException("Page type missing in entry JSON for " + entryId.toString() + " in page " + p);
                }
                p++;
            }
            completionAdvancement = ResourceLocation.tryParse(json.get("completionAdvancement").getAsString());
        }

        Entry entry = new Entry(entryId, tab, icon, x, y, pages.toArray(new Page[0]), parents.toArray(new ResourceLocation[0]), name, critical, incomplete, incompletePages.toArray(new Page[0]), completionAdvancement);
        return entry;
    }
    @Nonnull
    public static Entry fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        int x = buf.readInt();
        int y = buf.readInt();
        ItemStack icon = buf.readWithCodecTrusted(NbtOps.INSTANCE, ItemStack.CODEC);
        Component name = buf.readWithCodecTrusted(NbtOps.INSTANCE, ComponentSerialization.CODEC);
        Page[] pages = buf.readList(EntrySerializer::pageFromNetwork).toArray(new Page[0]);
        List<ResourceLocation> parents = buf.readList(FriendlyByteBuf::readResourceLocation);
        boolean critical = buf.readBoolean();
        ResourceLocation tab = buf.readResourceLocation();
        boolean incomplete = buf.readBoolean();
        Page[] incompletePages = buf.readList(EntrySerializer::pageFromNetwork).toArray(new Page[0]);
        ResourceLocation completionAdvancement = buf.readResourceLocation();
        Entry entry = new Entry(id, tab, icon, x, y, pages, parents.toArray(new ResourceLocation[0]), name, critical, incomplete, incompletePages, completionAdvancement);
        return entry;
    }
    public static Page pageFromNetwork(FriendlyByteBuf buf) {
        ResourceLocation type = buf.readResourceLocation();
        PageSerializer pageSerializer = DataNEssenceRegistries.PAGE_TYPE_REGISTRY.get(type);
        Page page = pageSerializer.fromNetwork(buf);
        return page;
    }
    public static void pageToNetwork(FriendlyByteBuf buf, Page page) {
        buf.writeResourceLocation(DataNEssenceRegistries.PAGE_TYPE_REGISTRY.getKey(page.getSerializer()));
        page.getSerializer().toNetwork(page, buf);
    }
    public static void toNetwork(FriendlyByteBuf buf, Entry entry) {
        buf.writeResourceLocation(entry.id);
        buf.writeInt(entry.x);
        buf.writeInt(entry.y);
        buf.writeWithCodec(NbtOps.INSTANCE, ItemStack.CODEC, entry.icon);
        buf.writeWithCodec(NbtOps.INSTANCE, ComponentSerialization.CODEC, entry.name);
        buf.writeCollection(Arrays.stream(entry.pages).toList(), EntrySerializer::pageToNetwork);
        List<ResourceLocation> parents = new ArrayList<>();
        for (Entry i : entry.getParentEntries()) {
            parents.add(i.id);
        }
        buf.writeCollection(parents, FriendlyByteBuf::writeResourceLocation);
        buf.writeBoolean(entry.critical);
        buf.writeResourceLocation(entry.tab);
        buf.writeBoolean(entry.incomplete);
        buf.writeCollection(Arrays.stream(entry.incompletePages).toList(), EntrySerializer::pageToNetwork);
        buf.writeResourceLocation(entry.completionAdvancement);
    }
}