package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public record UnlockedEntrySyncS2CPacket(List<ResourceLocation> unlocked, List<ResourceLocation> incomplete) implements Message {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<UnlockedEntrySyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "unlocked_entry_sync"));

    public static void write(RegistryFriendlyByteBuf buf, UnlockedEntrySyncS2CPacket obj) {
        buf.writeCollection(obj.unlocked, FriendlyByteBuf::writeResourceLocation);
        buf.writeCollection(obj.incomplete, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientPlayerUnlockedEntries.set(unlocked, incomplete);
        ClientDataNEssenceUtil.updateWorld();
    }

    public static UnlockedEntrySyncS2CPacket read(RegistryFriendlyByteBuf buf) {
        List<ResourceLocation> unlocked = buf.readList(ResourceLocation.STREAM_CODEC);
        List<ResourceLocation> incomplete = buf.readList(ResourceLocation.STREAM_CODEC);
        return new UnlockedEntrySyncS2CPacket(unlocked, incomplete);
    }
}