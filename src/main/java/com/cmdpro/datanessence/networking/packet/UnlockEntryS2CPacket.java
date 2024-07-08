package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.List;
import java.util.function.Supplier;

public record UnlockEntryS2CPacket(ResourceLocation unlocked) implements Message {
    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        Entry entry = Entries.entries.get(unlocked);
        if (entry != null) {
            if (!ClientPlayerUnlockedEntries.getUnlocked().contains(unlocked)) {
                ClientPlayerUnlockedEntries.getUnlocked().add(unlocked);
            }
            if (entry.critical) {
                ClientDataNEssenceUtil.unlockedCriticalData(entry);
            }
            ClientDataNEssenceUtil.updateWorld();
        }
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceLocation(unlocked);
    }
    public static final ResourceLocation ID = new ResourceLocation(DataNEssence.MOD_ID, "unlock_entry");
    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static UnlockEntryS2CPacket read(FriendlyByteBuf buf) {
        ResourceLocation unlocked = buf.readResourceLocation();
        return new UnlockEntryS2CPacket(unlocked);
    }
}