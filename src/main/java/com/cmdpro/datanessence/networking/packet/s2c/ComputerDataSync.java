package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.computers.ClientComputerData;
import com.cmdpro.datanessence.computers.ComputerData;
import com.cmdpro.datanessence.computers.ComputerTypeSerializer;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.ComputerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record ComputerDataSync(ComputerData data) implements Message {
    public static ComputerDataSync read(RegistryFriendlyByteBuf buf) {
        ComputerData data = ComputerTypeSerializer.STREAM_CODEC.decode(buf);
        return new ComputerDataSync(data);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientComputerData.clientComputerData = data;
        ClientHandler.openScreen();
    }

    public static void write(RegistryFriendlyByteBuf buf, ComputerDataSync obj) {
        ComputerTypeSerializer.STREAM_CODEC.encode(buf, obj.data);
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<ComputerDataSync> TYPE = new Type<>(DataNEssence.locate("computer_data_sync"));
    private static class ClientHandler {
        public static void openScreen() {
            Minecraft.getInstance().setScreen(new ComputerScreen(Component.empty()));
        }
    }
}