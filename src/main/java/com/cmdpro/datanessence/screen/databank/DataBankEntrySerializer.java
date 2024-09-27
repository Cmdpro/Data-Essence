package com.cmdpro.datanessence.screen.databank;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.databank.MinigameCreator;
import com.cmdpro.datanessence.api.databank.MinigameSerializer;
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

public class DataBankEntrySerializer {
    public DataBankEntry read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("icon")) {
            throw new JsonSyntaxException("Element color missing in data bank entry JSON for " + entryId.toString());
        }
        if (!json.has("tier")) {
            throw new JsonSyntaxException("Element tier missing in data bank entry JSON for " + entryId.toString());
        }
        if (!json.has("name")) {
            throw new JsonSyntaxException("Element name missing in data bank entry JSON for " + entryId.toString());
        }
        if (!json.has("minigames")) {
            throw new JsonSyntaxException("Element minigames missing in data bank entry JSON for " + entryId.toString());
        }
        if (!json.has("entry")) {
            throw new JsonSyntaxException("Element entry missing in data bank entry JSON for " + entryId.toString());
        }
        ItemStack icon = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(GsonHelper.getAsString(json, "icon"))));
        int tier = json.get("tier").getAsInt();
        Component name = Component.translatable(json.get("name").getAsString());
        List<MinigameCreator> minigames = new ArrayList<>();
        int o = 0;
        for (JsonElement i : json.getAsJsonArray("minigames")) {
            JsonObject obj = i.getAsJsonObject();
            if (obj.has("type")) {
                MinigameSerializer minigameSerializer = DataNEssenceRegistries.MINIGAME_TYPE_REGISTRY.get(ResourceLocation.tryParse(obj.get("type").getAsString()));
                MinigameCreator minigame = minigameSerializer.fromJson(obj);
                minigames.add(minigame);
            } else {
                throw new JsonSyntaxException("Page type missing in entry JSON for " + entryId.toString() + " in page " + o);
            }
            o++;
        }
        DataBankEntry entry = new DataBankEntry(entryId, icon, tier, minigames.toArray(new MinigameCreator[0]), name, ResourceLocation.tryParse(json.get("entry").getAsString()));
        return entry;
    }
    @Nonnull
    public static DataBankEntry fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        ItemStack icon = buf.readWithCodecTrusted(NbtOps.INSTANCE, ItemStack.CODEC);
        Component name = buf.readWithCodecTrusted(NbtOps.INSTANCE, ComponentSerialization.CODEC);
        int tier = buf.readInt();
        MinigameCreator[] minigames = buf.readList(DataBankEntrySerializer::minigameFromNetwork).toArray(new MinigameCreator[0]);
        ResourceLocation entry2 = buf.readResourceLocation();
        DataBankEntry entry = new DataBankEntry(id, icon, tier, minigames, name, entry2);
        return entry;
    }
    public static MinigameCreator minigameFromNetwork(FriendlyByteBuf buf) {
        ResourceLocation type = buf.readResourceLocation();
        MinigameSerializer minigameSerializer = DataNEssenceRegistries.MINIGAME_TYPE_REGISTRY.get(type);
        MinigameCreator minigame = minigameSerializer.fromNetwork(buf);
        return minigame;
    }
    public static void minigameToNetwork(FriendlyByteBuf buf, MinigameCreator creator) {
        buf.writeResourceLocation(DataNEssenceRegistries.MINIGAME_TYPE_REGISTRY.getKey(creator.getSerializer()));
        creator.getSerializer().toNetwork(creator, buf);
    }
    public static void toNetwork(FriendlyByteBuf buf, DataBankEntry entry) {
        buf.writeResourceLocation(entry.id);
        buf.writeWithCodec(NbtOps.INSTANCE, ItemStack.CODEC, entry.icon);
        buf.writeWithCodec(NbtOps.INSTANCE, ComponentSerialization.CODEC, entry.name);
        buf.writeInt(entry.tier);
        buf.writeCollection(Arrays.stream(entry.minigames).toList(), DataBankEntrySerializer::minigameToNetwork);
        buf.writeResourceLocation(entry.entry);
    }
}