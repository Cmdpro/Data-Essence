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

public record EntrySyncS2CPacket(Map<ResourceLocation, Entry> entries, Map<ResourceLocation, DataTab> tabs) implements Message {
    public static EntrySyncS2CPacket read(FriendlyByteBuf buf) {
        Map<ResourceLocation, Entry> entries = buf.readMap(FriendlyByteBuf::readResourceLocation, EntrySerializer::fromNetwork);
        Map<ResourceLocation, DataTab> tabs = buf.readMap(FriendlyByteBuf::readResourceLocation, DataTabSerializer::fromNetwork);
        return new EntrySyncS2CPacket(entries, tabs);
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