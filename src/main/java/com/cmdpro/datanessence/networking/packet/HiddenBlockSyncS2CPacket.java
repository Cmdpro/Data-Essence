package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlock;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksSerializer;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public record HiddenBlockSyncS2CPacket(Map<ResourceLocation, HiddenBlock> blocks) implements Message {

    public static void write(RegistryFriendlyByteBuf buf, HiddenBlockSyncS2CPacket obj) {
        buf.writeMap(obj.blocks, ResourceLocation.STREAM_CODEC, HiddenBlocksSerializer::toNetwork);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<HiddenBlockSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "hidden_block_sync"));

    public static HiddenBlockSyncS2CPacket read(RegistryFriendlyByteBuf buf) {
        Map<ResourceLocation, HiddenBlock> blocks = buf.readMap(ResourceLocation.STREAM_CODEC, HiddenBlocksSerializer::fromNetwork);
        return new HiddenBlockSyncS2CPacket(blocks);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        HiddenBlocksManager.blocks.clear();
        HiddenBlocksManager.blocks.putAll(blocks);
        ClientRenderingUtil.updateWorld();
    }
}