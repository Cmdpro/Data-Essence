package com.cmdpro.datanessence.screen.datatablet.pages.serializers;

import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.ItemPage;
import com.cmdpro.datanessence.screen.datatablet.pages.MultiblockPage;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

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
    public Codec<MultiblockPage> getCodec() {
        return CODEC.codec();
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MultiblockPage> getStreamCodec() {
        return STREAM_CODEC;
    }
}
