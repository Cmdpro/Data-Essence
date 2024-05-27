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
        net.messageBuilder(PlayerTierSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlayerTierSyncS2CPacket::new)
                .encoder(PlayerTierSyncS2CPacket::toBytes)
                .consumerMainThread(PlayerTierSyncS2CPacket::handle)
                .add();
        net.messageBuilder(DataBankEntrySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(DataBankEntrySyncS2CPacket::new)
                .encoder(DataBankEntrySyncS2CPacket::toBytes)
                .consumerMainThread(DataBankEntrySyncS2CPacket::handle)
                .add();
        net.messageBuilder(PlayerFinishDataBankMinigameC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PlayerFinishDataBankMinigameC2SPacket::new)
                .encoder(PlayerFinishDataBankMinigameC2SPacket::toBytes)
                .consumerMainThread(PlayerFinishDataBankMinigameC2SPacket::handle)
                .add();
        net.messageBuilder(PlayerChangeDriveDataC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PlayerChangeDriveDataC2SPacket::new)
                .encoder(PlayerChangeDriveDataC2SPacket::toBytes)
                .consumerMainThread(PlayerChangeDriveDataC2SPacket::handle)
                .add();
        net.messageBuilder(UnlockEntryS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(UnlockEntryS2CPacket::new)
                .encoder(UnlockEntryS2CPacket::toBytes)
                .consumerMainThread(UnlockEntryS2CPacket::handle)
                .add();

    }
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
