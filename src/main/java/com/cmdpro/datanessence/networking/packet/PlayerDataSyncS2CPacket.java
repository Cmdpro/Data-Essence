package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class PlayerDataSyncS2CPacket implements Message {
    private boolean[] unlockedEssences;
    private BlockPos linkPos;
    private Color linkColor;

    public static final ResourceLocation ID = new ResourceLocation(DataNEssence.MOD_ID, "player_data_sync");

    @Override
    public void write(FriendlyByteBuf buf) {
        if (unlockedEssences.length >= 1) {
            buf.writeBoolean(unlockedEssences[0]);
        } else {
            buf.writeBoolean(false);
        }
        if (unlockedEssences.length >= 2) {
            buf.writeBoolean(unlockedEssences[1]);
        } else {
            buf.writeBoolean(false);
        }
        if (unlockedEssences.length >= 3) {
            buf.writeBoolean(unlockedEssences[2]);
        } else {
            buf.writeBoolean(false);
        }
        if (unlockedEssences.length >= 4) {
            buf.writeBoolean(unlockedEssences[3]);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeBoolean(linkPos != null);
        if (linkPos != null) {
            buf.writeBlockPos(linkPos);
        }
        buf.writeInt(linkColor.getRed());
        buf.writeInt(linkColor.getGreen());
        buf.writeInt(linkColor.getBlue());
        buf.writeInt(linkColor.getAlpha());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
    public PlayerDataSyncS2CPacket(boolean[] unlockedEssences, BlockPos linkPos, Color linkColor) {
        this.unlockedEssences = unlockedEssences;
        this.linkPos = linkPos;
        this.linkColor = linkColor;
    }

    public PlayerDataSyncS2CPacket(FriendlyByteBuf buf) {
        read(buf);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        boolean essence = buf.readBoolean();
        boolean lunar = buf.readBoolean();
        boolean natural = buf.readBoolean();
        boolean exotic = buf.readBoolean();
        unlockedEssences = new boolean[]{essence, lunar, natural, exotic};
        boolean hasLinkPos = buf.readBoolean();
        if (hasLinkPos) {
            linkPos = buf.readBlockPos();
        } else {
            linkPos = null;
        }
        int r = buf.readInt();
        int g = buf.readInt();
        int b = buf.readInt();
        int a = buf.readInt();
        linkColor = new Color(r, g, b, a);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientPlayerData.set(unlockedEssences, linkPos, linkColor);
    }
}