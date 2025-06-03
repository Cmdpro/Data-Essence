package com.cmdpro.datanessence.data.datatablet;

import com.cmdpro.datanessence.api.datatablet.Page;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CompletionStage {
    public static final Codec<CompletionStage> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            ComponentSerialization.CODEC.optionalFieldOf("name_override").forGetter((stage) -> stage.nameOverride),
            ComponentSerialization.CODEC.optionalFieldOf("flavor_override").forGetter((stage) -> stage.flavorOverride),
            ItemStack.CODEC.optionalFieldOf("icon_override").forGetter((stage) -> stage.iconOverride),
            EntrySerializer.PAGE_CODEC.listOf().fieldOf("pages").forGetter((stage) -> stage.pages),
            ResourceLocation.CODEC.listOf().fieldOf("completion_advancements").forGetter((stage) -> stage.completionAdvancements)
    ).apply(builder, CompletionStage::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CompletionStage> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeOptional(pValue.nameOverride, (buffer, value) -> ComponentSerialization.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buffer, value));
        pBuffer.writeOptional(pValue.flavorOverride, (buffer, value) -> ComponentSerialization.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buffer, value));
        pBuffer.writeOptional(pValue.iconOverride, (buffer, value) -> ItemStack.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buffer, value));
        pBuffer.writeCollection(pValue.pages, (pBuffer1, pValue1) -> EntrySerializer.PAGE_STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer1, pValue1));
        pBuffer.writeCollection(pValue.completionAdvancements, FriendlyByteBuf::writeResourceLocation);
    }, pBuffer -> {
        Optional<Component> nameOverride = pBuffer.readOptional((buffer) -> ComponentSerialization.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buffer));
        Optional<Component> flavorOverride = pBuffer.readOptional((buffer) -> ComponentSerialization.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buffer));
        Optional<ItemStack> iconOverride = pBuffer.readOptional((buffer) -> ItemStack.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buffer));
        List<Page> pages = pBuffer.readList((pBuffer1) -> EntrySerializer.PAGE_STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer1));
        List<ResourceLocation> completionAdvancements = pBuffer.readList(FriendlyByteBuf::readResourceLocation);
        return new CompletionStage(nameOverride, flavorOverride, iconOverride, pages, completionAdvancements);
    });
    public CompletionStage(Optional<Component> nameOverride, Optional<Component> flavorOverride, Optional<ItemStack> iconOverride, List<Page> pages, List<ResourceLocation> completionAdvancements) {
        this.nameOverride = nameOverride;
        this.flavorOverride = flavorOverride;
        this.iconOverride = iconOverride;
        this.pages = pages;
        this.completionAdvancements = completionAdvancements;
    }
    public Optional<Component> nameOverride;
    public Optional<Component> flavorOverride;
    public Optional<ItemStack> iconOverride;
    public List<Page> pages;
    public List<ResourceLocation> completionAdvancements;
}
