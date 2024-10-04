package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.DataTabletUtil;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.databank.DataBankEntries;
import com.cmdpro.datanessence.databank.DataBankEntry;
import com.cmdpro.datanessence.datatablet.Entries;
import com.cmdpro.datanessence.datatablet.Entry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public record PlayerFinishDataBankMinigameC2SPacket(ResourceLocation entry) implements Message {

    public static PlayerFinishDataBankMinigameC2SPacket read(RegistryFriendlyByteBuf buf) {
        ResourceLocation entry = buf.readResourceLocation();
        return new PlayerFinishDataBankMinigameC2SPacket(entry);
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        DataBankEntry entry2 = DataBankEntries.entries.get(entry);
        if (entry2 != null) {
            if (entry2.tier <= player.getData(AttachmentTypeRegistry.TIER)) {
                Entry entry3 = Entries.entries.get(entry2.entry);
                DataTabletUtil.unlockEntry(player, entry2.entry, entry3.incomplete);
            }
        }
    }

    public static void write(RegistryFriendlyByteBuf buf, PlayerFinishDataBankMinigameC2SPacket obj) {
        buf.writeResourceLocation(obj.entry);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<PlayerFinishDataBankMinigameC2SPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "player_finish_data_bank_minigame"));
}