package com.cmdpro.datanessence.screen.datatablet.pages.serializers;

import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.CraftingPage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CraftingPageSerializer extends PageSerializer<CraftingPage> {
    public static final CraftingPageSerializer INSTANCE = new CraftingPageSerializer();
    @Override
    public CraftingPage fromJson(JsonObject json) {
        Component text = Component.translatable(json.get("text").getAsString());
        ResourceLocation font = json.has("font") ? ResourceLocation.tryParse(json.get("font").getAsString()) : Minecraft.DEFAULT_FONT;
        ArrayList<ResourceLocation> recipes = new ArrayList<>();
        for (JsonElement i : json.get("recipes").getAsJsonArray()) {
            recipes.add(ResourceLocation.tryParse(i.getAsString()));
        }
        return new CraftingPage(text, font, recipes.toArray(new ResourceLocation[0]));
    }

    @Override
    public CraftingPage fromNetwork(FriendlyByteBuf buf) {
        Component text = buf.readComponent();
        ResourceLocation font = buf.readResourceLocation();
        List<ResourceLocation> recipes = buf.readList(FriendlyByteBuf::readResourceLocation);
        return new CraftingPage(text, font, recipes.toArray(new ResourceLocation[0]));
    }

    @Override
    public void toNetwork(CraftingPage page, FriendlyByteBuf buf) {
        buf.writeComponent(page.text);
        buf.writeResourceLocation(page.font);
        buf.writeCollection(Arrays.stream(page.recipes).toList(), FriendlyByteBuf::writeResourceLocation);
    }
}
