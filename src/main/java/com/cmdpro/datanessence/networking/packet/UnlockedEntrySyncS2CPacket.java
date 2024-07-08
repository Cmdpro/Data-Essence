package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public record UnlockedEntrySyncS2CPacket(List<ResourceLocation> unlocked) implements Message {

    public static final ResourceLocation ID = new ResourceLocation(DataNEssence.MOD_ID, "unlocked_entry_sync");
    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(unlocked, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientPlayerUnlockedEntries.set(unlocked);
        ClientDataNEssenceUtil.updateWorld();
    }

    public static UnlockedEntrySyncS2CPacket read(FriendlyByteBuf buf) {
        List<ResourceLocation> unlocked = buf.readList(FriendlyByteBuf::readResourceLocation);
        return new UnlockedEntrySyncS2CPacket(unlocked);
    }
}