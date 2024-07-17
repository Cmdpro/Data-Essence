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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.List;
import java.util.function.Supplier;

public record UnlockEntryS2CPacket(ResourceLocation unlocked, boolean incomplete) implements Message {
    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        Entry entry = Entries.entries.get(unlocked);
        if (entry != null) {
            if (incomplete) {
                if (!ClientPlayerUnlockedEntries.getIncomplete().contains(unlocked)) {
                    ClientPlayerUnlockedEntries.getIncomplete().add(unlocked);
                }
            } else {
                ClientPlayerUnlockedEntries.getIncomplete().remove(unlocked);
                if (!ClientPlayerUnlockedEntries.getUnlocked().contains(unlocked)) {
                    ClientPlayerUnlockedEntries.getUnlocked().add(unlocked);
                }
                if (entry.critical) {
                    ClientDataNEssenceUtil.unlockedCriticalData(entry);
                }
                ClientDataNEssenceUtil.updateWorld();
            }
        }
    }

    public static void write(RegistryFriendlyByteBuf pBuffer, UnlockEntryS2CPacket obj) {
        pBuffer.writeResourceLocation(obj.unlocked);
        pBuffer.writeBoolean(obj.incomplete);
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<UnlockEntryS2CPacket> TYPE = new Type<>(new ResourceLocation(DataNEssence.MOD_ID, "unlock_entry"));

    public static UnlockEntryS2CPacket read(RegistryFriendlyByteBuf buf) {
        ResourceLocation unlocked = buf.readResourceLocation();
        boolean incomplete = buf.readBoolean();
        return new UnlockEntryS2CPacket(unlocked, incomplete);
    }

}