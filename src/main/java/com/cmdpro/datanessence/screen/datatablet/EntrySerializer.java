package com.cmdpro.datanessence.screen.datatablet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntrySerializer {
    public Entry read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("icon")) {
            throw new JsonSyntaxException("Element color missing in entry JSON for " + entryId.toString());
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
        ItemStack icon = CraftingHelper.getItemStack(json.getAsJsonObject("icon"), true, false);
        int x = json.get("x").getAsInt();
        int y = json.get("y").getAsInt();
        Component name = Component.translatable(json.get("name").getAsString());
        ResourceLocation parent = null;
        if (json.has("parent")) {
            parent = ResourceLocation.tryParse(json.get("parent").getAsString());
        }
        List<Page> pages = new ArrayList<>();
        int o = 0;
        for (JsonElement i : json.getAsJsonArray("pages")) {
            JsonObject obj = i.getAsJsonObject();
            if (obj.has("type")) {
                PageSerializer pageSerializer = DataNEssenceUtil.PAGE_TYPE_REGISTRY.get().getValue(ResourceLocation.tryParse(obj.get("type").getAsString()));
                Page page = pageSerializer.fromJson(obj);
                pages.add(page);
            } else {
                throw new JsonSyntaxException("Page type missing in entry JSON for " + entryId.toString() + " in page " + o);
            }
            o++;
        }
        Entry entry = new Entry(entryId, icon, x, y, pages.toArray(new Page[0]), parent, name);
        return entry;
    }
    @Nonnull
    public static Entry fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        ItemStack icon = buf.readItem();
        int x = buf.readInt();
        int y = buf.readInt();
        Component name = buf.readComponent();
        Page[] pages = buf.readList(EntrySerializer::pageFromNetwork).toArray(new Page[0]);
        boolean hasParent = buf.readBoolean();
        ResourceLocation parent = null;
        if (hasParent) {
            parent = buf.readResourceLocation();
        }
        Entry entry = new Entry(id, icon, x, y, pages, parent, name);
        return entry;
    }
    public static Page pageFromNetwork(FriendlyByteBuf buf) {
        ResourceLocation type = buf.readResourceLocation();
        PageSerializer pageSerializer = DataNEssenceUtil.PAGE_TYPE_REGISTRY.get().getValue(type);
        Page page = pageSerializer.fromNetwork(buf);
        return page;
    }
    public static void pageToNetwork(FriendlyByteBuf buf, Page page) {
        buf.writeResourceLocation(DataNEssenceUtil.PAGE_TYPE_REGISTRY.get().getKey(page.getSerializer()));
        page.getSerializer().toNetwork(page, buf);
    }
    public static void toNetwork(FriendlyByteBuf buf, Entry entry) {
        buf.writeResourceLocation(entry.id);
        buf.writeItem(entry.icon);
        buf.writeInt(entry.x);
        buf.writeInt(entry.y);
        buf.writeComponent(entry.name);
        buf.writeCollection(Arrays.stream(entry.pages).toList(), EntrySerializer::pageToNetwork);
        buf.writeBoolean(entry.getParentEntry() != null);
        if (entry.getParentEntry() != null) {
            buf.writeResourceLocation(entry.getParentEntry().id);
        }
    }
}