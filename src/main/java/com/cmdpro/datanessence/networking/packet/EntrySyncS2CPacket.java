package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.datatablet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EntrySyncS2CPacket implements Message {
    private Map<ResourceLocation, Entry> entries;
    private Map<ResourceLocation, DataTab> tabs;

    public EntrySyncS2CPacket(HashMap<ResourceLocation, Entry> entries, HashMap<ResourceLocation, DataTab> tabs) {
        this.entries = entries;
        this.tabs = tabs;
    }

    public EntrySyncS2CPacket(FriendlyByteBuf buf) {
        read(buf);
    }
    @Override
    public void read(FriendlyByteBuf buf) {
        this.entries = buf.readMap(FriendlyByteBuf::readResourceLocation, EntrySerializer::fromNetwork);
        this.tabs = buf.readMap(FriendlyByteBuf::readResourceLocation, DataTabSerializer::fromNetwork);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(entries, FriendlyByteBuf::writeResourceLocation, EntrySerializer::toNetwork);
        buf.writeMap(tabs, FriendlyByteBuf::writeResourceLocation, DataTabSerializer::toNetwork);
    }
    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        Entries.entries.clear();
        Entries.entries.putAll(entries);
        Entries.tabs.clear();
        Entries.tabs.putAll(tabs);
        for (Entry i : Entries.entries.values()) {
            i.updateParentEntries();
        }
    }

    public static final ResourceLocation ID = new ResourceLocation(DataNEssence.MOD_ID, "entry_sync");
    @Override
    public ResourceLocation id() {
        return ID;
    }
}