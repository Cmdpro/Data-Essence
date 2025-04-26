package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.databank.shaders.PostShaderManager;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.client.ClientModEvents;
import com.cmdpro.datanessence.client.gui.PingsGuiLayer;
import com.cmdpro.datanessence.client.shaders.PingShader;
import com.cmdpro.datanessence.data.pinging.StructurePing;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record PingStructures(List<StructurePing> structures) implements Message {
    public static PingStructures read(RegistryFriendlyByteBuf buf) {
        List<StructurePing> structures = buf.readList((pBuffer) -> StructurePing.STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer));
        return  new PingStructures(structures);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
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
        public static void addPings(List<StructurePing> pings) {
            for (StructurePing i : pings) {
                PingsGuiLayer.pings.put(i, 200);
            }
        }
    }
}