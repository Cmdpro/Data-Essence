package com.cmdpro.datanessence.computers;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.computer.ComputerFile;
import com.cmdpro.datanessence.api.computer.ComputerFileType;
import com.cmdpro.datanessence.api.databank.MinigameCreator;
import com.cmdpro.datanessence.api.databank.MinigameSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ComputerTypeSerializer {
    public static final Codec<ComputerFile> FILE_CODEC = DataNEssenceRegistries.COMPUTER_FILE_TYPES_REGISTRY.byNameCodec().dispatch(ComputerFile::getType, computerFileType -> computerFileType.getCodec());
    public static final StreamCodec<RegistryFriendlyByteBuf, ComputerFile> FILE_STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceLocation(DataNEssenceRegistries.COMPUTER_FILE_TYPES_REGISTRY.getKey(pValue.getType()));
        pValue.getType().getStreamCodec().encode(pBuffer, pValue);
    }, pBuffer -> {
        ResourceLocation type = pBuffer.readResourceLocation();
        ComputerFileType minigameSerializer = DataNEssenceRegistries.COMPUTER_FILE_TYPES_REGISTRY.get(type);
        ComputerFile page = (ComputerFile)minigameSerializer.getStreamCodec().decode(pBuffer);
        return page;
    });
    public static final MapCodec<ComputerData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FILE_CODEC.listOf().fieldOf("files").forGetter((data) -> data.files)
    ).apply(instance, ComputerData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ComputerData> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeCollection(pValue.files, (buf, val) -> FILE_STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf, val));
    }, pBuffer -> {
        List<ComputerFile> files = pBuffer.readList((buf) -> FILE_STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf));
        ComputerData data = new ComputerData(files);
        return data;
    });
    public ComputerData read(ResourceLocation entryId, JsonObject json) {
        ComputerData data = CODEC.codec().parse(JsonOps.INSTANCE, json).getOrThrow();
        return data;
    }
}