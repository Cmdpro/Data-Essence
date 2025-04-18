package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record UnlockedEntrySync(List<ResourceLocation> unlocked, List<ResourceLocation> incomplete) implements Message {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<UnlockedEntrySync> TYPE = new Type<>(DataNEssence.locate("unlocked_entry_sync"));

    public static void write(RegistryFriendlyByteBuf buf, UnlockedEntrySync obj) {
        buf.writeCollection(obj.unlocked, FriendlyByteBuf::writeResourceLocation);
        buf.writeCollection(obj.incomplete, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientPlayerUnlockedEntries.set(unlocked, incomplete);
        ClientRenderingUtil.updateWorld();
    }

    public static UnlockedEntrySync read(RegistryFriendlyByteBuf buf) {
        List<ResourceLocation> unlocked = buf.readList(ResourceLocation.STREAM_CODEC);
        List<ResourceLocation> incomplete = buf.readList(ResourceLocation.STREAM_CODEC);
        return new UnlockedEntrySync(unlocked, incomplete);
    }
}