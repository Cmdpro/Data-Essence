package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.moddata.ClientPlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class PlayerDataSyncS2CPacket {
    private final boolean[] unlockedEssences;
    private final BlockPos linkPos;
    private final Color linkColor;

    public PlayerDataSyncS2CPacket(boolean[] unlockedEssences, BlockPos linkPos, Color linkColor) {
        this.unlockedEssences = unlockedEssences;
        this.linkPos = linkPos;
        this.linkColor = linkColor;
    }

    public PlayerDataSyncS2CPacket(FriendlyByteBuf buf) {
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

    public void toBytes(FriendlyByteBuf buf) {
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

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePacket(this, supplier));
        });
        context.setPacketHandled(true);
    }

    public static class ClientPacketHandler {
        public static void handlePacket(PlayerDataSyncS2CPacket msg, Supplier<NetworkEvent.Context> supplier) {
            ClientPlayerData.set(msg.unlockedEssences, msg.linkPos, msg.linkColor);
        }
    }
}