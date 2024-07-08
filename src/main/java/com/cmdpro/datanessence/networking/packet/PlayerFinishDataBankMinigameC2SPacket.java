package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public record PlayerFinishDataBankMinigameC2SPacket(ResourceLocation entry) implements Message {

    public static PlayerFinishDataBankMinigameC2SPacket read(FriendlyByteBuf buf) {
        ResourceLocation entry = buf.readResourceLocation();
        return new PlayerFinishDataBankMinigameC2SPacket(entry);
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        DataBankEntry entry2 = DataBankEntries.entries.get(entry);
        if (entry2 != null) {
            if (entry2.tier <= player.getData(AttachmentTypeRegistry.TIER)) {
                DataNEssenceUtil.DataTabletUtil.unlockEntry(player, entry2.entry);
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(entry);
    }

    public static final ResourceLocation ID = new ResourceLocation(DataNEssence.MOD_ID, "player_finish_data_bank_minigame");
    @Override
    public ResourceLocation id() {
        return ID;
    }
}