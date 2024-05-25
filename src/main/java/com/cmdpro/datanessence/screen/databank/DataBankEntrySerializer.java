package com.cmdpro.datanessence.screen.databank;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.screen.datatablet.Page;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBankEntrySerializer {
    public DataBankEntry read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("icon")) {
            throw new JsonSyntaxException("Element color missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("tier")) {
            throw new JsonSyntaxException("Element tier missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("name")) {
            throw new JsonSyntaxException("Element name missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("minigames")) {
            throw new JsonSyntaxException("Element minigames missing in entry JSON for " + entryId.toString());
        }
        ItemStack icon = CraftingHelper.getItemStack(json.getAsJsonObject("icon"), true, false);
        int tier = json.get("tier").getAsInt();
        Component name = Component.translatable(json.get("name").getAsString());
        List<Minigame> minigames = new ArrayList<>();
        int o = 0;
        for (JsonElement i : json.getAsJsonArray("minigames")) {
            JsonObject obj = i.getAsJsonObject();
            if (obj.has("type")) {
                MinigameCreator minigameSerializer = DataNEssenceRegistries.MINIGAME_TYPE_REGISTRY.get().getValue(ResourceLocation.tryParse(obj.get("type").getAsString()));
                Minigame minigame = minigameSerializer.fromJson(obj);
                minigames.add(minigame);
            } else {
                throw new JsonSyntaxException("Page type missing in entry JSON for " + entryId.toString() + " in page " + o);
            }
            o++;
        }
        DataBankEntry entry = new DataBankEntry(entryId, icon, tier, minigames.toArray(new Minigame[0]), name);
        return entry;
    }
    @Nonnull
    public static DataBankEntry fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        ItemStack icon = buf.readItem();
        int tier = buf.readInt();
        Component name = buf.readComponent();
        Minigame[] minigames = buf.readList(DataBankEntrySerializer::minigameFromNetwork).toArray(new Minigame[0]);
        DataBankEntry entry = new DataBankEntry(id, icon, tier, minigames, name);
        return entry;
    }
    public static Minigame minigameFromNetwork(FriendlyByteBuf buf) {
        ResourceLocation type = buf.readResourceLocation();
        MinigameCreator minigameSerializer = DataNEssenceRegistries.MINIGAME_TYPE_REGISTRY.get().getValue(type);
        Minigame minigame = minigameSerializer.fromNetwork(buf);
        return minigame;
    }
    public static void minigameToNetwork(FriendlyByteBuf buf, Minigame page) {
        buf.writeResourceLocation(DataNEssenceRegistries.MINIGAME_TYPE_REGISTRY.get().getKey(page.getCreator()));
        page.getCreator().toNetwork(page, buf);
    }
    public static void toNetwork(FriendlyByteBuf buf, DataBankEntry entry) {
        buf.writeResourceLocation(entry.id);
        buf.writeItem(entry.icon);
        buf.writeInt(entry.tier);
        buf.writeComponent(entry.name);
        buf.writeCollection(Arrays.stream(entry.minigames).toList(), DataBankEntrySerializer::minigameToNetwork);
    }
}