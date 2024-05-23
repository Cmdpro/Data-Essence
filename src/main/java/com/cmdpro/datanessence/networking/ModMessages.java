package com.cmdpro.datanessence.networking;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {

    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(DataNEssence.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(PlayerDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlayerDataSyncS2CPacket::new)
                .encoder(PlayerDataSyncS2CPacket::toBytes)
                .consumerMainThread(PlayerDataSyncS2CPacket::handle)
                .add();
        net.messageBuilder(EntrySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(EntrySyncS2CPacket::new)
                .encoder(EntrySyncS2CPacket::toBytes)
                .consumerMainThread(EntrySyncS2CPacket::handle)
                .add();
        net.messageBuilder(UnlockedEntrySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(UnlockedEntrySyncS2CPacket::new)
                .encoder(UnlockedEntrySyncS2CPacket::toBytes)
                .consumerMainThread(UnlockedEntrySyncS2CPacket::handle)
                .add();

    }
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
