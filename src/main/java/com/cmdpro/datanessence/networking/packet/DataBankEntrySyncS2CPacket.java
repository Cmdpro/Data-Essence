package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntry;
import com.cmdpro.datanessence.screen.databank.DataBankEntrySerializer;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.screen.datatablet.EntrySerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record DataBankEntrySyncS2CPacket(Map<ResourceLocation, DataBankEntry> entries) implements Message {
    public static DataBankEntrySyncS2CPacket read(RegistryFriendlyByteBuf buf) {
        Map<ResourceLocation, DataBankEntry> entries = buf.readMap(FriendlyByteBuf::readResourceLocation, DataBankEntrySerializer::fromNetwork);
        return  new DataBankEntrySyncS2CPacket(entries);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        DataBankEntries.clientEntries.clear();
        DataBankEntries.clientEntries.putAll(entries);
        ClientHandler.openScreen();
    }

    public static void write(RegistryFriendlyByteBuf buf, DataBankEntrySyncS2CPacket obj) {
        buf.writeMap(obj.entries, ResourceLocation.STREAM_CODEC, DataBankEntrySerializer::toNetwork);
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<DataBankEntrySyncS2CPacket> TYPE = new Type<>(new ResourceLocation(DataNEssence.MOD_ID, "data_bank_entry_sync"));
    public static class ClientHandler {
        public static void openScreen() {
            Minecraft.getInstance().setScreen(new DataBankScreen(Component.empty()));
        }
    }
}