package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.databank.DataBankEntries;
import com.cmdpro.datanessence.databank.DataBankEntry;
import com.cmdpro.datanessence.databank.DataBankEntrySerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public record DataBankEntrySyncS2CPacket(Map<ResourceLocation, DataBankEntry> entries) implements Message {
    public static DataBankEntrySyncS2CPacket read(RegistryFriendlyByteBuf buf) {
        Map<ResourceLocation, DataBankEntry> entries = buf.readMap(FriendlyByteBuf::readResourceLocation, (pBuffer) -> DataBankEntrySerializer.STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer));
        return  new DataBankEntrySyncS2CPacket(entries);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        DataBankEntries.clientEntries.clear();
        DataBankEntries.clientEntries.putAll(entries);
        ClientHandler.openScreen();
    }

    public static void write(RegistryFriendlyByteBuf buf, DataBankEntrySyncS2CPacket obj) {
        buf.writeMap(obj.entries, ResourceLocation.STREAM_CODEC, (pBuffer, pValue) -> DataBankEntrySerializer.STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer, pValue));
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<DataBankEntrySyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "data_bank_entry_sync"));
    public static class ClientHandler {
        public static void openScreen() {
            Minecraft.getInstance().setScreen(new DataBankScreen(Component.empty()));
        }
    }
}