package com.cmdpro.datanessence.networking;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.computers.ComputerData;
import com.cmdpro.datanessence.multiblock.MultiblockSerializer;
import com.cmdpro.datanessence.networking.packet.*;
import com.cmdpro.datanessence.recipe.ShapedFabricationRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.concurrent.Callable;
import java.util.function.Function;

@EventBusSubscriber(modid = DataNEssence.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModMessages {
    public class Handler {
        public static <T extends CustomPacketPayload> void handle(T message, IPayloadContext ctx) {
            if (message instanceof Message msg) {
                if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
                    ctx.enqueueWork(() -> {
                        Server.handle(msg, ctx);
                    });
                } else {
                    ctx.enqueueWork(() -> {
                        Client.handle(msg, ctx);
                    });
                }
            }
        }
        public class Client {
            public static <T extends Message> void handle(T message, IPayloadContext ctx) {
                message.handleClient(Minecraft.getInstance(), Minecraft.getInstance().player);
            }
        }
        public class Server {
            public static <T extends Message> void handle(T message, IPayloadContext ctx) {
                message.handleServer(ctx.player().getServer(), (ServerPlayer)ctx.player());
            }
        }
        public abstract interface Reader<T extends Message> {
            public abstract T read(RegistryFriendlyByteBuf buf);
        }
        public abstract interface Writer<T extends Message> {
            public abstract void write(RegistryFriendlyByteBuf buf, T message);
        }
    }
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(DataNEssence.MOD_ID)
                .versioned("1.0");

        //S2C
        registrar.playToClient(UnlockEntryS2CPacket.TYPE, getNetworkCodec(UnlockEntryS2CPacket::read, UnlockEntryS2CPacket::write), Handler::handle);
        registrar.playToClient(UnlockedEntrySyncS2CPacket.TYPE, getNetworkCodec(UnlockedEntrySyncS2CPacket::read, UnlockedEntrySyncS2CPacket::write), Handler::handle);
        registrar.playToClient(PlayerTierSyncS2CPacket.TYPE, getNetworkCodec(PlayerTierSyncS2CPacket::read, PlayerTierSyncS2CPacket::write), Handler::handle);
        registrar.playToClient(PlayerDataSyncS2CPacket.TYPE, getNetworkCodec(PlayerDataSyncS2CPacket::read, PlayerDataSyncS2CPacket::write), Handler::handle);
        registrar.playToClient(DataBankEntrySyncS2CPacket.TYPE, getNetworkCodec(DataBankEntrySyncS2CPacket::read, DataBankEntrySyncS2CPacket::write), Handler::handle);
        registrar.playToClient(ComputerDataSyncS2CPacket.TYPE, getNetworkCodec(ComputerDataSyncS2CPacket::read, ComputerDataSyncS2CPacket::write), Handler::handle);
        registrar.playToClient(HiddenBlockSyncS2CPacket.TYPE, getNetworkCodec(HiddenBlockSyncS2CPacket::read, HiddenBlockSyncS2CPacket::write), Handler::handle);
        registrar.playToClient(EntrySyncS2CPacket.TYPE, getNetworkCodec(EntrySyncS2CPacket::read, EntrySyncS2CPacket::write), Handler::handle);
        registrar.playToClient(DragonPartsSyncS2CPacket.TYPE, getNetworkCodec(DragonPartsSyncS2CPacket::read, DragonPartsSyncS2CPacket::write), Handler::handle);
        registrar.playToClient(MultiblockSyncS2CPacket.TYPE, getNetworkCodec(MultiblockSyncS2CPacket::read, MultiblockSyncS2CPacket::write), Handler::handle);
        //C2S
        registrar.playToServer(PlayerFinishDataBankMinigameC2SPacket.TYPE, getNetworkCodec(PlayerFinishDataBankMinigameC2SPacket::read, PlayerFinishDataBankMinigameC2SPacket::write), Handler::handle);
        registrar.playToServer(PlayerChangeDriveDataC2SPacket.TYPE, getNetworkCodec(PlayerChangeDriveDataC2SPacket::read, PlayerChangeDriveDataC2SPacket::write), Handler::handle);
        registrar.playToServer(PlayerSetItemHandlerLockedC2SPacket.TYPE, getNetworkCodec(PlayerSetItemHandlerLockedC2SPacket::read, PlayerSetItemHandlerLockedC2SPacket::write), Handler::handle);
    }

    public static <T extends Message> StreamCodec<RegistryFriendlyByteBuf, T> getNetworkCodec(Handler.Reader<T> reader, Handler.Writer<T> writer) {
        return StreamCodec.of(writer::write, reader::read);
    }

    public static <T extends Message> void sendToServer(T message) {
        PacketDistributor.sendToServer(message);
    }

    public static <T extends Message> void sendToPlayer(T message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }
    public static <T extends Message> void sendToPlayersTrackingEntityAndSelf(T message, ServerPlayer player) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, message);
    }
}
