package com.cmdpro.datanessence.screen.datatablet.pages.serializers;

import com.cmdpro.datanessence.screen.datatablet.Page;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.TextPage;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class TextPageSerializer extends PageSerializer<TextPage> {
    public static final TextPageSerializer INSTANCE = new TextPageSerializer();
    @Override
    public TextPage fromJson(JsonObject json) {
        ResourceLocation font = json.has("font") ? ResourceLocation.tryParse(json.get("font").getAsString()) : Minecraft.DEFAULT_FONT;
        MutableComponent text = Component.translatable(json.get("text").getAsString());
        text = text.withStyle(text.getStyle().withFont(font));
        return new TextPage(text);
    }

    @Override
    public TextPage fromNetwork(FriendlyByteBuf buf) {
        Component text = buf.readComponent();
        return new TextPage(text);
    }

    @Override
    public void toNetwork(TextPage page, FriendlyByteBuf buf) {
        buf.writeComponent(page.text);
    }
}
