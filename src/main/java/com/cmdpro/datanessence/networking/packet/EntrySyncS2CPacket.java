package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.datatablet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record EntrySyncS2CPacket(Map<ResourceLocation, Entry> entries, Map<ResourceLocation, DataTab> tabs) implements Message {
    public static EntrySyncS2CPacket read(FriendlyByteBuf buf) {
        Map<ResourceLocation, Entry> entries = buf.readMap(ResourceLocation.STREAM_CODEC, EntrySerializer::fromNetwork);
        Map<ResourceLocation, DataTab> tabs = buf.readMap(ResourceLocation.STREAM_CODEC, DataTabSerializer::fromNetwork);
        return new EntrySyncS2CPacket(entries, tabs);
    }

    public static void write(RegistryFriendlyByteBuf buf, EntrySyncS2CPacket obj) {
        buf.writeMap(obj.entries, ResourceLocation.STREAM_CODEC, EntrySerializer::toNetwork);
        buf.writeMap(obj.tabs, ResourceLocation.STREAM_CODEC, DataTabSerializer::toNetwork);
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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<EntrySyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "entry_sync"));
}