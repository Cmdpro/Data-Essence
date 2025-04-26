package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.databank.shaders.PostShaderManager;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.client.gui.PingsGuiLayer;
import com.cmdpro.datanessence.data.pinging.StructurePing;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record CreatePingShader(Vec3 position) implements Message {
    public static CreatePingShader read(RegistryFriendlyByteBuf buf) {
        Vec3 position = buf.readVec3();
        return new CreatePingShader(position);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientHandler.startShader(position);
    }

    public static void write(RegistryFriendlyByteBuf buf, CreatePingShader obj) {
        buf.writeVec3(obj.position);
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<CreatePingShader> TYPE = new Type<>(DataNEssence.locate("create_ping_shader"));
    private static class ClientHandler {
        public static void startShader(Vec3 position) {
            PostShaderInstance shader = new com.cmdpro.datanessence.client.shaders.PingShader(position.toVector3f());
            shader.setActive(true);
            PostShaderManager.addShader(shader);
        }
    }
}