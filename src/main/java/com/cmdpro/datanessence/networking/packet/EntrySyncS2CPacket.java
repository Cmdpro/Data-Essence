package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.screen.datatablet.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EntrySyncS2CPacket {
    private final Map<ResourceLocation, Entry> entries;
    private final Map<ResourceLocation, DataTab> tabs;

    public EntrySyncS2CPacket(HashMap<ResourceLocation, Entry> entries, HashMap<ResourceLocation, DataTab> tabs) {
        this.entries = entries;
        this.tabs = tabs;
    }

    public EntrySyncS2CPacket(FriendlyByteBuf buf) {
        this.entries = buf.readMap(FriendlyByteBuf::readResourceLocation, EntrySerializer::fromNetwork);
        this.tabs = buf.readMap(FriendlyByteBuf::readResourceLocation, DataTabSerializer::fromNetwork);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(entries, FriendlyByteBuf::writeResourceLocation, EntrySerializer::toNetwork);
        buf.writeMap(tabs, FriendlyByteBuf::writeResourceLocation, DataTabSerializer::toNetwork);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientPacketHandler.handlePacket(this, supplier);
        });
        return true;
    }
    public static class ClientPacketHandler {
        public static void handlePacket(EntrySyncS2CPacket msg, Supplier<NetworkEvent.Context> supplier) {
            Entries.entries.clear();
            Entries.entries.putAll(msg.entries);
            Entries.tabs.clear();
            Entries.tabs.putAll(msg.tabs);
            for (Entry i : Entries.entries.values()) {
                i.updateParentEntry();
            }
        }
    }
}