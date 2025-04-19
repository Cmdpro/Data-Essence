package com.cmdpro.datanessence.data.datatablet.pages.serializers;

import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.data.datatablet.pages.TextPage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

public class TextPageSerializer extends PageSerializer<TextPage> {
    public static final TextPageSerializer INSTANCE = new TextPageSerializer();
    public static final StreamCodec<RegistryFriendlyByteBuf, TextPage> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        ComponentSerialization.STREAM_CODEC.encode(pBuffer, pValue.text);
        pBuffer.writeBoolean(pValue.rtl);
    }, (pBuffer) -> {
        Component text = ComponentSerialization.STREAM_CODEC.decode(pBuffer);
        boolean rtl = pBuffer.readBoolean();
        return new TextPage(text, rtl);
    });
    public static final MapCodec<TextPage> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ComponentSerialization.CODEC.fieldOf("text").forGetter(page -> page.text),
            Codec.BOOL.optionalFieldOf("rtl", false).forGetter(page -> page.rtl)
    ).apply(instance, TextPage::new));
    @Override
    public MapCodec<TextPage> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TextPage> getStreamCodec() {
        return STREAM_CODEC;
    }
}
