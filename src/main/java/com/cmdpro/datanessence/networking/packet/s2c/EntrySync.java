package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.data.datatablet.*;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public record EntrySync(Map<ResourceLocation, Entry> entries, List<ResourceLocation> sortedEntries, Map<ResourceLocation, DataTab> tabs) implements Message {
    public static EntrySync read(FriendlyByteBuf buf) {
        Map<ResourceLocation, Entry> entries = buf.readMap(ResourceLocation.STREAM_CODEC, (pBuffer) -> EntrySerializer.STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer));
        List<ResourceLocation> sortedEntries = buf.readList(ResourceLocation.STREAM_CODEC);
        Map<ResourceLocation, DataTab> tabs = buf.readMap(ResourceLocation.STREAM_CODEC, (pBuffer) -> DataTabSerializer.STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer));
        return new EntrySync(entries, sortedEntries, tabs);
    }

    public static void write(RegistryFriendlyByteBuf buf, EntrySync obj) {
        buf.writeMap(obj.entries, ResourceLocation.STREAM_CODEC, (pBuffer, pValue) -> EntrySerializer.STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer, pValue));
        buf.writeCollection(obj.sortedEntries, ResourceLocation.STREAM_CODEC);
        buf.writeMap(obj.tabs, ResourceLocation.STREAM_CODEC,(pBuffer, pValue) -> DataTabSerializer.STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer, pValue));
    }
    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientHandler.handle(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<EntrySync> TYPE = new Type<>(DataNEssence.locate("entry_sync"));

    private static class ClientHandler {
        public static void handle(EntrySync packet) {
            Entries.entries.clear();
            Entries.entries.putAll(packet.entries);
            Entries.tabs.clear();
            Entries.tabs.putAll(packet.tabs);
            for (Entry i : Entries.entries.values()) {
                i.updateParentEntries();
            }
            Entries.tabsSorted.clear();
            for (ResourceLocation i : packet.sortedEntries) {
                DataTab tab = Entries.tabs.get(i);
                if (tab != null) {
                    Entries.tabsSorted.add(tab);
                }
            }
            DataTabletScreen.savedEntry = null;
        }
    }
}