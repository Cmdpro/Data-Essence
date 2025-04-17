package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.databank.shaders.PostShaderManager;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.client.ClientModEvents;
import com.cmdpro.datanessence.databank.DataBankEntries;
import com.cmdpro.datanessence.databank.DataBankEntry;
import com.cmdpro.datanessence.databank.DataBankEntrySerializer;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.SoundRegistry;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public record PingStructures(List<StructurePing> structures) implements Message {
    public static PingStructures read(RegistryFriendlyByteBuf buf) {
        List<StructurePing> structures = buf.readList((pBuffer) -> StructurePing.STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer));
        return  new PingStructures(structures);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientHandler.startShader();
    }

    public static void write(RegistryFriendlyByteBuf buf, PingStructures obj) {
        buf.writeCollection(obj.structures, (pBuffer, pValue) -> StructurePing.STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer, pValue));
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<PingStructures> TYPE = new Type<>(DataNEssence.locate("ping_structures"));
    private static class ClientHandler {
        public static void startShader() {
            ClientModEvents.pingShader.time = 0;
            ClientModEvents.pingShader.setActive(true);
        }
    }
    public record StructurePing(BlockPos pos, ResourceLocation type) {
        public static final StreamCodec<RegistryFriendlyByteBuf, StructurePing> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
            buffer.writeBlockPos(value.pos);
            buffer.writeResourceLocation(value.type);
        }, buffer -> {
            BlockPos pos = buffer.readBlockPos();
            ResourceLocation type = buffer.readResourceLocation();
            return new StructurePing(pos, type);
        });
    }
}