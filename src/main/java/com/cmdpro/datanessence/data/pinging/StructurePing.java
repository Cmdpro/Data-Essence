package com.cmdpro.datanessence.data.pinging;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class StructurePing {
    private PingableStructure pingableStructure;
    public BlockPos pos;
    public ResourceLocation type;
    public boolean known;

    public StructurePing(BlockPos pos, ResourceLocation type, boolean known) {
        this.pos = pos;
        this.type = type;
        this.known = known;
    }

    public PingableStructure getPingableStructure() {
        if (pingableStructure == null) {
            pingableStructure = PingableStructureManager.types.get(type);
        }
        return pingableStructure;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, StructurePing> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
        buffer.writeBlockPos(value.pos);
        buffer.writeResourceLocation(value.type);
        buffer.writeBoolean(value.known);
    }, buffer -> {
        BlockPos pos = buffer.readBlockPos();
        ResourceLocation type = buffer.readResourceLocation();
        boolean known = buffer.readBoolean();
        return new StructurePing(pos, type, known);
    });
}
