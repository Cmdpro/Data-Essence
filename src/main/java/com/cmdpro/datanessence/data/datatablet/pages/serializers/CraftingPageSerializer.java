package com.cmdpro.datanessence.data.datatablet.pages.serializers;

import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.data.datatablet.pages.CraftingPage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class CraftingPageSerializer extends PageSerializer<CraftingPage> {
    public static final CraftingPageSerializer INSTANCE = new CraftingPageSerializer();
    public static final StreamCodec<RegistryFriendlyByteBuf, CraftingPage> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        ComponentSerialization.STREAM_CODEC.encode(pBuffer, pValue.text);
        pBuffer.writeBoolean(pValue.rtl);
        pBuffer.writeCollection(pValue.recipes, FriendlyByteBuf::writeResourceLocation);
    }, (pBuffer) -> {
        Component text = ComponentSerialization.STREAM_CODEC.decode(pBuffer);
        boolean rtl = pBuffer.readBoolean();
        List<ResourceLocation> recipes = pBuffer.readList(FriendlyByteBuf::readResourceLocation);
        return new CraftingPage(text, rtl, recipes);
    });
    public static final MapCodec<CraftingPage> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("text", Component.empty()).forGetter(page -> page.text),
            Codec.BOOL.optionalFieldOf("rtl", false).forGetter(page -> page.rtl),
            ResourceLocation.CODEC.listOf().fieldOf("recipes").forGetter(page -> page.recipes)
    ).apply(instance, CraftingPage::new));
    @Override
    public MapCodec<CraftingPage> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CraftingPage> getStreamCodec() {
        return STREAM_CODEC;
    }
}
