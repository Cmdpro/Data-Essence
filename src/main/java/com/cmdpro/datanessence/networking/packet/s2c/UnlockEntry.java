package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UnlockEntry(ResourceLocation unlocked, int completionStage) implements Message {
    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext ctx) {
        Entry entry = Entries.entries.get(unlocked);
        if (entry != null) {
            if (entry.completionStages.size() > completionStage) {
                ClientPlayerUnlockedEntries.getIncomplete().put(unlocked, completionStage);
            } else {
                ClientPlayerUnlockedEntries.getIncomplete().remove(unlocked);
                if (!ClientPlayerUnlockedEntries.getUnlocked().contains(unlocked)) {
                    ClientPlayerUnlockedEntries.getUnlocked().add(unlocked);
                }
                if (entry.critical) {
                    ClientRenderingUtil.unlockedCriticalData(entry);
                }
            }
            ClientRenderingUtil.updateWorld();
        }
    }

    public static void write(RegistryFriendlyByteBuf pBuffer, UnlockEntry obj) {
        pBuffer.writeResourceLocation(obj.unlocked);
        pBuffer.writeInt(obj.completionStage);
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<UnlockEntry> TYPE = new Type<>(DataNEssence.locate("unlock_entry"));

    public static UnlockEntry read(RegistryFriendlyByteBuf buf) {
        ResourceLocation unlocked = buf.readResourceLocation();
        int completionStage = buf.readInt();
        return new UnlockEntry(unlocked, completionStage);
    }

}