package com.cmdpro.datanessence.screen.datatablet.pages.serializers;

import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.CraftingPage;
import com.cmdpro.datanessence.screen.datatablet.pages.ItemPage;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemPageSerializer extends PageSerializer<ItemPage> {
    public static final ItemPageSerializer INSTANCE = new ItemPageSerializer();
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemPage> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        ComponentSerialization.STREAM_CODEC.encode(pBuffer, pValue.text);
        pBuffer.writeBoolean(pValue.rtl);
        ItemStack.STREAM_CODEC.encode(pBuffer, pValue.item);
    }, (pBuffer) -> {
        Component text = ComponentSerialization.STREAM_CODEC.decode(pBuffer);
        boolean rtl = pBuffer.readBoolean();
        ItemStack item = ItemStack.STREAM_CODEC.decode(pBuffer);
        return new ItemPage(text, rtl, item);
    });
    public static final MapCodec<ItemPage> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("text", Component.empty()).forGetter(page -> page.text),
            Codec.BOOL.optionalFieldOf("rtl", false).forGetter(page -> page.rtl),
            ItemStack.CODEC.fieldOf("item").forGetter(page -> page.item)
    ).apply(instance, ItemPage::new));
    @Override
    public MapCodec<ItemPage> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemPage> getStreamCodec() {
        return STREAM_CODEC;
    }
}
