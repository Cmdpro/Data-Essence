package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
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

public record PlayerDataSyncS2CPacket(boolean[] unlockedEssences, BlockPos linkPos, Color linkColor) implements Message {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<PlayerDataSyncS2CPacket> TYPE = new Type<>(new ResourceLocation(DataNEssence.MOD_ID, "player_data_sync"));

    public static void write(RegistryFriendlyByteBuf buf, PlayerDataSyncS2CPacket obj) {
        if (obj.unlockedEssences.length >= 1) {
            buf.writeBoolean(obj.unlockedEssences[0]);
        } else {
            buf.writeBoolean(false);
        }
        if (obj.unlockedEssences.length >= 2) {
            buf.writeBoolean(obj.unlockedEssences[1]);
        } else {
            buf.writeBoolean(false);
        }
        if (obj.unlockedEssences.length >= 3) {
            buf.writeBoolean(obj.unlockedEssences[2]);
        } else {
            buf.writeBoolean(false);
        }
        if (obj.unlockedEssences.length >= 4) {
            buf.writeBoolean(obj.unlockedEssences[3]);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeBoolean(obj.linkPos != null);
        if (obj.linkPos != null) {
            buf.writeBlockPos(obj.linkPos);
        }
        buf.writeInt(obj.linkColor.getRed());
        buf.writeInt(obj.linkColor.getGreen());
        buf.writeInt(obj.linkColor.getBlue());
        buf.writeInt(obj.linkColor.getAlpha());
    }
    public static PlayerDataSyncS2CPacket read(RegistryFriendlyByteBuf buf) {
        boolean essence = buf.readBoolean();
        boolean lunar = buf.readBoolean();
        boolean natural = buf.readBoolean();
        boolean exotic = buf.readBoolean();
        boolean[] unlockedEssences = new boolean[]{essence, lunar, natural, exotic};
        boolean hasLinkPos = buf.readBoolean();
        BlockPos linkPos = null;
        if (hasLinkPos) {
            linkPos = buf.readBlockPos();
        }
        int r = buf.readInt();
        int g = buf.readInt();
        int b = buf.readInt();
        int a = buf.readInt();
        Color linkColor = new Color(r, g, b, a);
        return new PlayerDataSyncS2CPacket(unlockedEssences, linkPos, linkColor);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientPlayerData.set(unlockedEssences, linkPos, linkColor);
    }
}