package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntry;
import com.cmdpro.datanessence.screen.databank.DataBankEntrySerializer;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.screen.datatablet.EntrySerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DataBankEntrySyncS2CPacket {
    private final Map<ResourceLocation, DataBankEntry> entries;

    public DataBankEntrySyncS2CPacket(Map<ResourceLocation, DataBankEntry> entries) {
        this.entries = entries;
    }

    public DataBankEntrySyncS2CPacket(FriendlyByteBuf buf) {
        this.entries = buf.readMap(FriendlyByteBuf::readResourceLocation, DataBankEntrySerializer::fromNetwork);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(entries, FriendlyByteBuf::writeResourceLocation, DataBankEntrySerializer::toNetwork);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DataBankEntries.clientEntries.clear();
            for (Map.Entry<ResourceLocation, DataBankEntry> i : entries.entrySet()) {
                DataBankEntries.clientEntries.put(i.getKey(), i.getValue());
            }
        });
        return true;
    }
}