package com.cmdpro.datanessence.api.util;

import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.DataBankEntrySync;
import com.cmdpro.datanessence.data.databank.DataBankEntries;
import com.cmdpro.datanessence.data.databank.DataBankEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class DataBankUtil {
    public static void sendDataBankEntries(ServerPlayer player, ResourceLocation[] ids) {
        Map<ResourceLocation, DataBankEntry> entries = new HashMap<>();
        for (ResourceLocation i : ids) {
            DataBankEntry entry = DataBankEntries.entries.get(i);
            if (entry != null) {
                entries.put(i, entry);
            }
        }
        ModMessages.sendToPlayer(new DataBankEntrySync(entries), (player));
    }

    public static void sendDataBankEntries(Player player, ResourceLocation[] ids) {
        DataBankUtil.sendDataBankEntries((ServerPlayer) player, ids);
    }
}
