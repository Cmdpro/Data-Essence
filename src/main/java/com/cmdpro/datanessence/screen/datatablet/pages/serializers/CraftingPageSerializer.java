package com.cmdpro.datanessence.screen.datatablet.pages.serializers;

import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.CraftingPage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CraftingPageSerializer extends PageSerializer<CraftingPage> {
    public static final CraftingPageSerializer INSTANCE = new CraftingPageSerializer();
    @Override
    public CraftingPage fromJson(JsonObject json) {
        ResourceLocation font = json.has("font") ? ResourceLocation.tryParse(json.get("font").getAsString()) : ResourceLocation.withDefaultNamespace("default");
        MutableComponent text = Component.translatable(json.get("text").getAsString());
        text = text.withStyle(text.getStyle().withFont(font));
        boolean rtl = json.has("rtl") ? json.get("rtl").getAsBoolean() : false;
        ArrayList<ResourceLocation> recipes = new ArrayList<>();
        for (JsonElement i : json.get("recipes").getAsJsonArray()) {
            recipes.add(ResourceLocation.tryParse(i.getAsString()));
        }
        return new CraftingPage(text, rtl, recipes.toArray(new ResourceLocation[0]));
    }

    @Override
    public CraftingPage fromNetwork(FriendlyByteBuf buf) {
        Component text = buf.readWithCodecTrusted(NbtOps.INSTANCE, ComponentSerialization.CODEC);
        boolean rtl = buf.readBoolean();
        List<ResourceLocation> recipes = buf.readList(FriendlyByteBuf::readResourceLocation);
        return new CraftingPage(text, rtl, recipes.toArray(new ResourceLocation[0]));
    }

    @Override
    public void toNetwork(CraftingPage page, FriendlyByteBuf buf) {
        buf.writeWithCodec(NbtOps.INSTANCE, ComponentSerialization.CODEC, page.text);
        buf.writeBoolean(page.rtl);
        buf.writeCollection(Arrays.stream(page.recipes).toList(), FriendlyByteBuf::writeResourceLocation);
    }
}
