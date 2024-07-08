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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DataBankEntrySyncS2CPacket implements Message {
    private Map<ResourceLocation, DataBankEntry> entries;

    public DataBankEntrySyncS2CPacket(Map<ResourceLocation, DataBankEntry> entries) {
        this.entries = entries;
    }

    public DataBankEntrySyncS2CPacket(FriendlyByteBuf buf) {
        read(buf);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.entries = buf.readMap(FriendlyByteBuf::readResourceLocation, DataBankEntrySerializer::fromNetwork);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        DataBankEntries.clientEntries.clear();
        DataBankEntries.clientEntries.putAll(entries);
        Minecraft.getInstance().setScreen(new DataBankScreen(Component.empty()));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(entries, FriendlyByteBuf::writeResourceLocation, DataBankEntrySerializer::toNetwork);
    }
    public static ResourceLocation ID = new ResourceLocation(DataNEssence.MOD_ID, "data_bank_entry_sync");
    @Override
    public ResourceLocation id() {
        return ID;
    }
}