package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.toasts.TierToast;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record PlayerTierSyncS2CPacket(int tier, boolean showIndicator) implements Message {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<PlayerTierSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "player_tier_sync"));

    public static void write(RegistryFriendlyByteBuf pBuffer, PlayerTierSyncS2CPacket obj) {
        pBuffer.writeInt(obj.tier);
        pBuffer.writeBoolean(obj.showIndicator);
    }

    public static PlayerTierSyncS2CPacket read(RegistryFriendlyByteBuf buf) {
        int tier = buf.readInt();
        boolean showIndicator = buf.readBoolean();
        return new PlayerTierSyncS2CPacket(tier, showIndicator);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientPlayerData.setTier(tier);
        if (showIndicator) {
            ClientRenderingUtil.progressionShader();
            ClientHandler.addToast(tier);
        }
    }
    public static class ClientHandler {
        public static void addToast(int tier) {
            Minecraft.getInstance().getToasts().addToast(new TierToast(tier));
        }
    }
}