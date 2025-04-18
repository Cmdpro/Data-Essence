package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.client.ClientModEvents;
import com.cmdpro.datanessence.client.gui.PingsGuiLayer;
import com.cmdpro.datanessence.data.pinging.PingableStructure;
import com.cmdpro.datanessence.data.pinging.PingableStructureManager;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record PingStructures(List<StructurePing> structures) implements Message {
    public static PingStructures read(RegistryFriendlyByteBuf buf) {
        List<StructurePing> structures = buf.readList((pBuffer) -> StructurePing.STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer));
        return  new PingStructures(structures);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientHandler.startShader();
        ClientHandler.addPings(structures);
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
        public static void addPings(List<StructurePing> pings) {
            for (StructurePing i : pings) {
                PingsGuiLayer.pings.put(i, 200);
            }
        }
    }
    public static class StructurePing {
        private PingableStructure pingableStructure;
        public BlockPos pos;
        public ResourceLocation type;
        public StructurePing(BlockPos pos, ResourceLocation type) {
            this.pos = pos;
            this.type = type;
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
        }, buffer -> {
            BlockPos pos = buffer.readBlockPos();
            ResourceLocation type = buffer.readResourceLocation();
            return new StructurePing(pos, type);
        });
    }
}