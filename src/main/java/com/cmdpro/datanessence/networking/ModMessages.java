package com.cmdpro.datanessence.networking;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.packet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@Mod.EventBusSubscriber(modid = DataNEssence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMessages {
    public class Handler {
        public static <T extends CustomPacketPayload> void handle(T message, PlayPayloadContext ctx) {
            if (message instanceof Message msg) {
                if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
                    ctx.workHandler().submitAsync(() -> {
                        Server.handle(msg, ctx);
                    });
                } else {
                    ctx.workHandler().submitAsync(() -> {
                        Client.handle(msg, ctx);
                    });
                }
            }
        }
        public class Client {
            public static <T extends Message> void handle(T message, PlayPayloadContext ctx) {
                message.handleClient(Minecraft.getInstance(), Minecraft.getInstance().player);
            }
        }
        public class Server {
            public static <T extends Message> void handle(T message, PlayPayloadContext ctx) {
                message.handleServer(ctx.level().get().getServer(), (ServerPlayer)ctx.player().get());
            }
        }
    }
    @SubscribeEvent
    public static void register(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(DataNEssence.MOD_ID)
                .versioned("1.0");

        //S2C
        registrar.play(UnlockEntryS2CPacket.ID, UnlockEntryS2CPacket::new, Handler::handle);
        registrar.play(UnlockedEntrySyncS2CPacket.ID, UnlockedEntrySyncS2CPacket::new, Handler::handle);
        registrar.play(PlayerTierSyncS2CPacket.ID, PlayerTierSyncS2CPacket::new, Handler::handle);
        registrar.play(PlayerDataSyncS2CPacket.ID, PlayerDataSyncS2CPacket::new, Handler::handle);
        registrar.play(HiddenBlockSyncS2CPacket.ID, HiddenBlockSyncS2CPacket::new, Handler::handle);
        registrar.play(EntrySyncS2CPacket.ID, EntrySyncS2CPacket::new, Handler::handle);
        registrar.play(DataBankEntrySyncS2CPacket.ID, DataBankEntrySyncS2CPacket::new, Handler::handle);
        //C2S
        registrar.play(PlayerFinishDataBankMinigameC2SPacket.ID, PlayerFinishDataBankMinigameC2SPacket::new, Handler::handle);
        registrar.play(PlayerChangeDriveDataC2SPacket.ID, PlayerChangeDriveDataC2SPacket::new, Handler::handle);
    }
    public static <T extends Message> void sendToServer(T message) {
        PacketDistributor.SERVER.noArg().send(message);
    }

    public static <T extends Message> void sendToPlayer(T message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(message);
    }
}
