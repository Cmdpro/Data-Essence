package com.cmdpro.datanessence.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface Message extends CustomPacketPayload {
    default void handleClient(Minecraft minecraft, Player player, IPayloadContext ctx) {

    }
    default void handleServer(MinecraftServer server, ServerPlayer player, IPayloadContext ctx) {

    }
}
