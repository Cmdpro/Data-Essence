package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.toasts.CriticalDataToast;
import com.cmdpro.datanessence.toasts.TierToast;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.TitleCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.awt.*;
import java.util.function.Supplier;

public class PlayerTierSyncS2CPacket {
    private final int tier;
    private final boolean showIndicator;

    public PlayerTierSyncS2CPacket(int tier, boolean showIndicator) {
        this.tier = tier;
        this.showIndicator = showIndicator;
    }

    public PlayerTierSyncS2CPacket(FriendlyByteBuf buf) {
        tier = buf.readInt();
        showIndicator = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(tier);
        buf.writeBoolean(showIndicator);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePacket(this, supplier));
        });
        context.setPacketHandled(true);
    }

    public static class ClientPacketHandler {
        public static void handlePacket(PlayerTierSyncS2CPacket msg, Supplier<NetworkEvent.Context> supplier) {
            ClientPlayerData.setTier(msg.tier);
            if (msg.showIndicator) {
                ClientDataNEssenceUtil.progressionShader();
                Minecraft.getInstance().getToasts().addToast(new TierToast(msg.tier));
            }
        }
    }
}