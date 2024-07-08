package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.toasts.CriticalDataToast;
import com.cmdpro.datanessence.toasts.TierToast;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.function.Supplier;

public record PlayerTierSyncS2CPacket(int tier, boolean showIndicator) implements Message {

    public static final ResourceLocation ID = new ResourceLocation(DataNEssence.MOD_ID, "player_tier_sync");
    @Override
    public ResourceLocation id() {
        return ID;
    }
    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(tier);
        pBuffer.writeBoolean(showIndicator);
    }

    public static PlayerTierSyncS2CPacket read(FriendlyByteBuf buf) {
        int tier = buf.readInt();
        boolean showIndicator = buf.readBoolean();
        return new PlayerTierSyncS2CPacket(tier, showIndicator);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientPlayerData.setTier(tier);
        if (showIndicator) {
            ClientDataNEssenceUtil.progressionShader();
            Minecraft.getInstance().getToasts().addToast(new TierToast(tier));
        }
    }
}