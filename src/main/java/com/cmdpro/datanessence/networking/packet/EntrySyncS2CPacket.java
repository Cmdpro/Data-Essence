package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.screen.datatablet.EntrySerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EntrySyncS2CPacket {
    private final Map<ResourceLocation, Entry> entries;

    public EntrySyncS2CPacket(HashMap<ResourceLocation, Entry> entries) {
        this.entries = entries;
    }

    public EntrySyncS2CPacket(FriendlyByteBuf buf) {
        this.entries = buf.readMap(FriendlyByteBuf::readResourceLocation, EntrySerializer::fromNetwork);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(entries, FriendlyByteBuf::writeResourceLocation, EntrySerializer::toNetwork);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Entries.entries.clear();
            for (Map.Entry<ResourceLocation, Entry> i : entries.entrySet()) {
                Entries.entries.put(i.getKey(), i.getValue());
            }
            for (Entry i : Entries.entries.values()) {
                i.updateParentEntry();
            }
        });
        return true;
    }
}