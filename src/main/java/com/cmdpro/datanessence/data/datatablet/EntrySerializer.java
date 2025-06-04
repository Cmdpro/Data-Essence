package com.cmdpro.datanessence.data.datatablet;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.datatablet.Page;
import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EntrySerializer {
    public Entry read(ResourceLocation entryId, JsonObject json) {
        Entry entry = ICondition.getWithWithConditionsCodec(CODEC, JsonOps.INSTANCE, json).orElse(null);
        if (entry != null) {
            entry.id = entryId;
            return entry;
        } else {
            return null;
        }
    }
    public static final Codec<Page> PAGE_CODEC = DataNEssenceRegistries.PAGE_TYPE_REGISTRY.byNameCodec().dispatch(Page::getSerializer, pageSerializer -> pageSerializer.getCodec());
    public static final StreamCodec<RegistryFriendlyByteBuf, Page> PAGE_STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceLocation(DataNEssenceRegistries.PAGE_TYPE_REGISTRY.getKey(pValue.getSerializer()));
        pValue.getSerializer().getStreamCodec().encode(pBuffer, pValue);
    }, pBuffer -> {
        ResourceLocation type = pBuffer.readResourceLocation();
        PageSerializer pageSerializer = DataNEssenceRegistries.PAGE_TYPE_REGISTRY.get(type);
        Page page = (Page)pageSerializer.getStreamCodec().decode(pBuffer);
        return page;
    });
    public static final MapCodec<Entry> ORIGINAL_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ItemStack.CODEC.fieldOf("icon").forGetter((entry) -> entry.icon),
            Codec.INT.fieldOf("x").forGetter((entry) -> entry.x),
            Codec.INT.fieldOf("y").forGetter((entry) -> entry.y),
            ComponentSerialization.CODEC.fieldOf("name").forGetter((entry) -> entry.name),
            ComponentSerialization.CODEC.optionalFieldOf("flavor", Component.empty()).forGetter((entry -> entry.flavor)),
            ResourceLocation.CODEC.listOf().optionalFieldOf("parents", new ArrayList<>()).forGetter((entry) -> entry.parents),
            PAGE_CODEC.listOf().fieldOf("pages").forGetter((entry) -> entry.pages),
            Codec.BOOL.optionalFieldOf("critical", false).forGetter((entry) -> entry.critical),
            ResourceLocation.CODEC.fieldOf("tab").forGetter((entry) -> entry.tab),
            CompletionStage.CODEC.listOf().optionalFieldOf("completion_stages", new ArrayList<>()).forGetter((entry) -> entry.completionStages),
            Codec.BOOL.optionalFieldOf("is_default", false).forGetter((entry) -> entry.isDefault)
    ).apply(instance, (icon, x, y, name, flavor, parents, pages, critical, tab, completionStages, isDefault) -> new Entry(null, tab, icon, x, y, pages, parents, name, flavor, critical, completionStages, isDefault)));
    public static final Codec<Optional<WithConditions<Entry>>> CODEC = ConditionalOps.createConditionalCodecWithConditions(ORIGINAL_CODEC.codec());
    public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceLocation(pValue.id);
        pBuffer.writeInt(pValue.x);
        pBuffer.writeInt(pValue.y);
        ItemStack.STREAM_CODEC.encode(pBuffer, pValue.icon);
        ComponentSerialization.STREAM_CODEC.encode(pBuffer, pValue.name);
        ComponentSerialization.STREAM_CODEC.encode(pBuffer, pValue.flavor);
        pBuffer.writeCollection(pValue.pages, (pBuffer1, pValue1) -> PAGE_STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer1, pValue1));
        List<ResourceLocation> parents = new ArrayList<>();
        for (Entry i : pValue.getParentEntries()) {
            parents.add(i.id);
        }
        pBuffer.writeCollection(parents, FriendlyByteBuf::writeResourceLocation);
        pBuffer.writeBoolean(pValue.critical);
        pBuffer.writeResourceLocation(pValue.tab);
        pBuffer.writeCollection(pValue.completionStages, (pBuffer1, pValue1) -> CompletionStage.STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer1, pValue1));
        pBuffer.writeBoolean(pValue.isDefault);
    }, pBuffer -> {
        ResourceLocation id = pBuffer.readResourceLocation();
        int x = pBuffer.readInt();
        int y = pBuffer.readInt();
        ItemStack icon = ItemStack.STREAM_CODEC.decode(pBuffer);
        Component name = ComponentSerialization.STREAM_CODEC.decode(pBuffer);
        Component flavor = ComponentSerialization.STREAM_CODEC.decode(pBuffer);
        List<Page> pages = pBuffer.readList((pBuffer1) -> PAGE_STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer1));
        List<ResourceLocation> parents = pBuffer.readList(FriendlyByteBuf::readResourceLocation);
        boolean critical = pBuffer.readBoolean();
        ResourceLocation tab = pBuffer.readResourceLocation();
        List<CompletionStage> completionStages = pBuffer.readList((pBuffer1) -> CompletionStage.STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer1));
        boolean isDefault = pBuffer.readBoolean();
        return new Entry(id, tab, icon, x, y, pages, parents, name, flavor, critical, completionStages, isDefault);
    });
}