package com.cmdpro.datanessence.data.pinging;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.computer.ComputerFile;
import com.cmdpro.datanessence.api.computer.ComputerFileType;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class PingableStructureSerializer {
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
    public static final MapCodec<PingableStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.STRUCTURE).fieldOf("structure").forGetter((data) -> data.structure),
            ResourceKey.codec(Registries.ADVANCEMENT).fieldOf("advancement").forGetter((data) -> data.advancement),
            ResourceKey.codec(Registries.ADVANCEMENT).optionalFieldOf("requiredAdvancement").forGetter((data) -> data.requiredAdvancement),
            Codec.INT.listOf().comapFlatMap(
                    integer -> Util.fixedSize(integer, 3)
                            .map(integers -> new Color(integers.get(0), integers.get(1), integers.get(2))),
                    color -> List.of(color.getRed(), color.getGreen(), color.getBlue())).fieldOf("color1").forGetter(data -> data.color1),
            Codec.INT.listOf().comapFlatMap(
                    integer -> Util.fixedSize(integer, 3)
                            .map(integers -> new Color(integers.get(0), integers.get(1), integers.get(2))),
                    color -> List.of(color.getRed(), color.getGreen(), color.getBlue())).fieldOf("color2").forGetter(data -> data.color2),
            PingableStructure.PingableStructureIcon.CODEC.fieldOf("icon").forGetter(data -> data.icon)
    ).apply(instance, PingableStructure::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PingableStructure> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceKey(pValue.structure);
        pBuffer.writeResourceKey(pValue.advancement);
        pBuffer.writeOptional(pValue.requiredAdvancement, FriendlyByteBuf::writeResourceKey);
        pBuffer.writeInt(pValue.color1.getRGB());
        pBuffer.writeInt(pValue.color2.getRGB());
        PingableStructure.PingableStructureIcon.STREAM_CODEC.encode(pBuffer, pValue.icon);
    }, pBuffer -> {
        ResourceKey<Structure> structure = pBuffer.readResourceKey(Registries.STRUCTURE);
        ResourceKey<Advancement> advancement = pBuffer.readResourceKey(Registries.ADVANCEMENT);
        Optional<ResourceKey<Advancement>> requiredAdvancement = pBuffer.readOptional((buf) -> buf.readResourceKey(Registries.ADVANCEMENT));
        int color1 = pBuffer.readInt();
        int color2 = pBuffer.readInt();
        PingableStructure.PingableStructureIcon icon = PingableStructure.PingableStructureIcon.STREAM_CODEC.decode(pBuffer);
        PingableStructure data = new PingableStructure(structure, advancement, requiredAdvancement, new Color(color1), new Color(color2), icon);
        return data;
    });
    public PingableStructure read(ResourceLocation entryId, JsonObject json) {
        PingableStructure data = CODEC.codec().parse(JsonOps.INSTANCE, json).getOrThrow();
        return data;
    }
}