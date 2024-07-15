package com.cmdpro.datanessence.screen.datatablet.pages.serializers;

import com.cmdpro.datanessence.computers.files.TextFile;
import com.cmdpro.datanessence.screen.datatablet.Page;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.TextPage;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class TextPageSerializer extends PageSerializer<TextPage> {
    public static final TextPageSerializer INSTANCE = new TextPageSerializer();
    @Override
    public TextPage fromJson(JsonObject json) {
        ResourceLocation font = json.has("font") ? ResourceLocation.tryParse(json.get("font").getAsString()) : Minecraft.DEFAULT_FONT;
        MutableComponent text = Component.translatable(json.get("text").getAsString());
        text = text.withStyle(text.getStyle().withFont(font));
        boolean rtl = json.has("rtl") ? json.get("rtl").getAsBoolean() : false;
        return new TextPage(text, rtl);
    }
    @Override
    public TextPage fromNetwork(FriendlyByteBuf buf) {
        Component text = buf.readWithCodecTrusted(NbtOps.INSTANCE, ComponentSerialization.CODEC);
        boolean rtl = buf.readBoolean();
        return new TextPage(text, rtl);
    }

    @Override
    public void toNetwork(TextPage page, FriendlyByteBuf buf) {
        buf.writeWithCodec(NbtOps.INSTANCE, ComponentSerialization.CODEC, page.text);
        buf.writeBoolean(page.rtl);
    }
}
