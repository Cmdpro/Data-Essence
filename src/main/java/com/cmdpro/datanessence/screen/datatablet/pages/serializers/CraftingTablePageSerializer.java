package com.cmdpro.datanessence.screen.datatablet.pages.serializers;

import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.CraftingTablePage;
import com.cmdpro.datanessence.screen.datatablet.pages.TextPage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CraftingTablePageSerializer extends PageSerializer<CraftingTablePage> {
    public static final CraftingTablePageSerializer INSTANCE = new CraftingTablePageSerializer();
    @Override
    public CraftingTablePage fromJson(JsonObject json) {
        Component text = Component.translatable(json.get("text").getAsString());
        ArrayList<ResourceLocation> recipes = new ArrayList<>();
        for (JsonElement i : json.get("text").getAsJsonArray()) {
            recipes.add(ResourceLocation.tryParse(i.getAsString()));
        }
        return new CraftingTablePage(text, recipes.toArray(new ResourceLocation[0]));
    }

    @Override
    public CraftingTablePage fromNetwork(FriendlyByteBuf buf) {
        Component text = buf.readComponent();
        List<ResourceLocation> recipes = buf.readList(FriendlyByteBuf::readResourceLocation);
        return new CraftingTablePage(text, recipes.toArray(new ResourceLocation[0]));
    }

    @Override
    public void toNetwork(CraftingTablePage page, FriendlyByteBuf buf) {
        buf.writeComponent(page.text);
        buf.writeCollection(Arrays.stream(page.recipes).toList(), FriendlyByteBuf::writeResourceLocation);
    }
}
