package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.data.datatablet.*;
import com.cmdpro.datanessence.data.pinging.PingableStructure;
import com.cmdpro.datanessence.data.pinging.PingableStructureManager;
import com.cmdpro.datanessence.data.pinging.PingableStructureSerializer;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record PingableSync(Map<ResourceLocation, PingableStructure> structures) implements Message {
    public static PingableSync read(FriendlyByteBuf buf) {
        Map<ResourceLocation, PingableStructure> structures = buf.readMap(ResourceLocation.STREAM_CODEC, (pBuffer) -> PingableStructureSerializer.STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer));
        return new PingableSync(structures);
    }

    public static void write(RegistryFriendlyByteBuf buf, PingableSync obj) {
        buf.writeMap(obj.structures, ResourceLocation.STREAM_CODEC, (pBuffer, pValue) -> PingableStructureSerializer.STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer, pValue));
    }
    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext ctx) {
        ClientHandler.handle(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<PingableSync> TYPE = new Type<>(DataNEssence.locate("pingable_sync"));

    private static class ClientHandler {
        public static void handle(PingableSync packet) {
            PingableStructureManager.types.clear();
            PingableStructureManager.types.putAll(packet.structures);
        }
    }
}