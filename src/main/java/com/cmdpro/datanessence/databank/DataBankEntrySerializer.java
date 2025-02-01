package com.cmdpro.datanessence.databank;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.databank.MinigameCreator;
import com.cmdpro.datanessence.api.databank.MinigameSerializer;
import com.cmdpro.datanessence.datatablet.DataTab;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public class DataBankEntrySerializer {
    public DataBankEntry read(ResourceLocation entryId, JsonObject json) {
        DataBankEntry entry = ICondition.getWithWithConditionsCodec(CODEC, JsonOps.INSTANCE, json).orElse(null);
        if (entry != null) {
            entry.id = entryId;
            return entry;
        } else {
            return null;
        }
    }
    public static final Codec<MinigameCreator> MINIGAME_CODEC = DataNEssenceRegistries.MINIGAME_TYPE_REGISTRY.byNameCodec().dispatch(MinigameCreator::getSerializer, MinigameSerializer::getCodec);
    public static final StreamCodec<RegistryFriendlyByteBuf, MinigameCreator> MINIGAME_STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceLocation(DataNEssenceRegistries.MINIGAME_TYPE_REGISTRY.getKey(pValue.getSerializer()));
        pValue.getSerializer().getStreamCodec().encode(pBuffer, pValue);
    }, pBuffer -> {
        ResourceLocation type = pBuffer.readResourceLocation();
        MinigameSerializer minigameSerializer = DataNEssenceRegistries.MINIGAME_TYPE_REGISTRY.get(type);
        MinigameCreator page = (MinigameCreator)minigameSerializer.getStreamCodec().decode(pBuffer);
        return page;
    });
    public static final MapCodec<DataBankEntry> ORIGINAL_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ItemStack.CODEC.fieldOf("icon").forGetter((entry) -> entry.icon),
            Codec.INT.fieldOf("tier").forGetter((entry) -> entry.tier),
            ComponentSerialization.CODEC.fieldOf("name").forGetter((entry) -> entry.name),
            MINIGAME_CODEC.listOf().optionalFieldOf("minigames", new ArrayList<>()).forGetter((entry) -> List.of(entry.minigames)),
            ResourceLocation.CODEC.fieldOf("entry").forGetter((entry) -> entry.entry)
    ).apply(instance, (icon, tier, name, minigames, entry) -> new DataBankEntry(null, icon, tier, minigames.toArray(new MinigameCreator[0]), name, entry)));
    public static final Codec<Optional<WithConditions<DataBankEntry>>> CODEC = ConditionalOps.createConditionalCodecWithConditions(ORIGINAL_CODEC.codec());
    public static final StreamCodec<RegistryFriendlyByteBuf, DataBankEntry> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceLocation(pValue.id);
        ItemStack.STREAM_CODEC.encode(pBuffer, pValue.icon);
        ComponentSerialization.STREAM_CODEC.encode(pBuffer, pValue.name);
        pBuffer.writeInt(pValue.tier);
        pBuffer.writeCollection(Arrays.stream(pValue.minigames).toList(), (pBuffer1, pValue1) -> MINIGAME_STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer1, pValue1));
        pBuffer.writeResourceLocation(pValue.entry);
    }, pBuffer -> {
        ResourceLocation id = pBuffer.readResourceLocation();
        ItemStack icon = ItemStack.STREAM_CODEC.decode(pBuffer);
        Component name = ComponentSerialization.STREAM_CODEC.decode(pBuffer);
        int tier = pBuffer.readInt();
        MinigameCreator[] minigames = pBuffer.readList((pBuffer1) -> MINIGAME_STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer1)).toArray(new MinigameCreator[0]);
        ResourceLocation entry2 = pBuffer.readResourceLocation();
        DataBankEntry entry = new DataBankEntry(id, icon, tier, minigames, name, entry2);
        return entry;
    });
}