package com.cmdpro.datanessence.screen.datatablet.pages.serializers;

import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.ItemPage;
import com.cmdpro.datanessence.screen.datatablet.pages.TextPage;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class ItemPageSerializer extends PageSerializer<ItemPage> {
    public static final ItemPageSerializer INSTANCE = new ItemPageSerializer();
    @Override
    public ItemPage fromJson(JsonObject json) {
        ResourceLocation font = json.has("font") ? ResourceLocation.tryParse(json.get("font").getAsString()) : new ResourceLocation("default");
        MutableComponent text = json.has("text") ? Component.translatable(json.get("text").getAsString()) : Component.empty();
        text = text.withStyle(text.getStyle().withFont(font));
        boolean rtl = json.has("rtl") ? json.get("rtl").getAsBoolean() : false;
        ItemStack item = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(GsonHelper.getAsString(json, "item"))));
        return new ItemPage(text, rtl, item);
    }
    @Override
    public ItemPage fromNetwork(FriendlyByteBuf buf) {
        Component text = buf.readWithCodecTrusted(NbtOps.INSTANCE, ComponentSerialization.CODEC);
        boolean rtl = buf.readBoolean();
        ItemStack item = buf.readWithCodecTrusted(NbtOps.INSTANCE, ItemStack.CODEC);
        return new ItemPage(text, rtl, item);
    }

    @Override
    public void toNetwork(ItemPage page, FriendlyByteBuf buf) {
        buf.writeWithCodec(NbtOps.INSTANCE, ComponentSerialization.CODEC, page.text);
        buf.writeBoolean(page.rtl);
        buf.writeWithCodec(NbtOps.INSTANCE, ItemStack.CODEC, page.item);
    }
}
