package com.cmdpro.datanessence.data.datatablet.pages.serializers;

import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.data.datatablet.pages.MultiblockPage;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class MultiblockPageSerializer extends PageSerializer<MultiblockPage> {
    public static final MultiblockPageSerializer INSTANCE = new MultiblockPageSerializer();
    public static final StreamCodec<RegistryFriendlyByteBuf, MultiblockPage> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceLocation(pValue.multiblock);
    }, (pBuffer) -> {
        ResourceLocation multiblock = pBuffer.readResourceLocation();
        return new MultiblockPage(multiblock);
    });
    public static final MapCodec<MultiblockPage> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("multiblock").forGetter(page -> page.multiblock)
    ).apply(instance, MultiblockPage::new));
    @Override
    public MapCodec<MultiblockPage> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MultiblockPage> getStreamCodec() {
        return STREAM_CODEC;
    }
}
