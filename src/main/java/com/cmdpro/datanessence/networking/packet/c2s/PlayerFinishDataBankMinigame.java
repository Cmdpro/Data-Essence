package com.cmdpro.datanessence.networking.packet.c2s;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.DataTabletUtil;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.data.databank.DataBankEntries;
import com.cmdpro.datanessence.data.databank.DataBankEntry;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public record PlayerFinishDataBankMinigame(ResourceLocation entry) implements Message {

    public static PlayerFinishDataBankMinigame read(RegistryFriendlyByteBuf buf) {
        ResourceLocation entry = buf.readResourceLocation();
        return new PlayerFinishDataBankMinigame(entry);
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        DataBankEntry entry2 = DataBankEntries.entries.get(entry);
        if (entry2 != null) {
            if (entry2.tier <= player.getData(AttachmentTypeRegistry.TIER)) {
                DataTabletUtil.unlockEntry(player, entry2.entry, 0);
            }
        }
    }

    public static void write(RegistryFriendlyByteBuf buf, PlayerFinishDataBankMinigame obj) {
        buf.writeResourceLocation(obj.entry);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<PlayerFinishDataBankMinigame> TYPE = new Type<>(DataNEssence.locate("player_finish_data_bank_minigame"));
}