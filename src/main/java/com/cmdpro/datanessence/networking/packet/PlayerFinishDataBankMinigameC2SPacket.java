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

public class PlayerFinishDataBankMinigameC2SPacket implements Message {
    private ResourceLocation entry;

    public PlayerFinishDataBankMinigameC2SPacket(ResourceLocation entry) {
        this.entry = entry;
    }

    public PlayerFinishDataBankMinigameC2SPacket(FriendlyByteBuf buf) {
        read(buf);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.entry = buf.readResourceLocation();
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