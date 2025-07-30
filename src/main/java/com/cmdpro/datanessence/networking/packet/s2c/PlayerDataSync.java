package com.cmdpro.datanessence.networking.packet.s2c;

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
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.awt.*;
import java.util.Map;

public record PlayerDataSync(Map<ResourceLocation, Boolean> unlockedEssences, BlockPos linkPos, Color linkColor) implements Message {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<PlayerDataSync> TYPE = new Type<>(DataNEssence.locate("player_data_sync"));

    public static void write(RegistryFriendlyByteBuf buf, PlayerDataSync obj) {
        buf.writeMap(obj.unlockedEssences, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeBoolean);
        buf.writeBoolean(obj.linkPos != null);
        if (obj.linkPos != null) {
            buf.writeBlockPos(obj.linkPos);
        }
        buf.writeInt(obj.linkColor.getRed());
        buf.writeInt(obj.linkColor.getGreen());
        buf.writeInt(obj.linkColor.getBlue());
        buf.writeInt(obj.linkColor.getAlpha());
    }
    public static PlayerDataSync read(RegistryFriendlyByteBuf buf) {
        Map<ResourceLocation, Boolean> unlockedEssences = buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readBoolean);
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
        return new PlayerDataSync(unlockedEssences, linkPos, linkColor);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext ctx) {
        ClientPlayerData.set(unlockedEssences, linkPos, linkColor);
    }
}