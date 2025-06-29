package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.data.pinging.PingableStructure;
import com.cmdpro.datanessence.data.pinging.PingableStructureManager;
import com.cmdpro.datanessence.data.pinging.PingableStructureSerializer;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.dev.DataTabletEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public record OpenEntryEditor() implements Message {
    public static OpenEntryEditor read(FriendlyByteBuf buf) {
        return new OpenEntryEditor();
    }

    public static void write(RegistryFriendlyByteBuf buf, OpenEntryEditor obj) {

    }
    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientHandler.handle(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<OpenEntryEditor> TYPE = new Type<>(DataNEssence.locate("open_entry_editor"));

    private static class ClientHandler {
        public static void handle(OpenEntryEditor packet) {
            Minecraft.getInstance().setScreen(new DataTabletEditorScreen(Component.empty()));
        }
    }
}