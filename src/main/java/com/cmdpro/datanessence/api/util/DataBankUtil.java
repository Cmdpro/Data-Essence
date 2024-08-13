package com.cmdpro.datanessence.api.util;

import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.DataBankEntrySyncS2CPacket;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class DataBankUtil {
    public static void sendDataBankEntries(ServerPlayer player, ResourceLocation[] ids) {
        Map<ResourceLocation, DataBankEntry> entries = new HashMap<>();
        for (ResourceLocation i : ids) {
            entries.put(i, DataBankEntries.entries.get(i));
        }
        ModMessages.sendToPlayer(new DataBankEntrySyncS2CPacket(entries), (player));
    }

    public static void sendDataBankEntries(Player player, ResourceLocation[] ids) {
        DataBankUtil.sendDataBankEntries((ServerPlayer) player, ids);
    }
}
