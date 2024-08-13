package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.multiblock.Multiblock;
import com.cmdpro.datanessence.multiblock.MultiblockManager;
import com.cmdpro.datanessence.multiblock.MultiblockSerializer;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public record MultiblockSyncS2CPacket(Map<ResourceLocation, Multiblock> multiblocks) implements Message {

    public static void write(RegistryFriendlyByteBuf buf, MultiblockSyncS2CPacket obj) {
        buf.writeMap(obj.multiblocks, ResourceLocation.STREAM_CODEC, MultiblockSerializer::toNetwork);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<MultiblockSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "multiblock_sync"));

    public static MultiblockSyncS2CPacket read(RegistryFriendlyByteBuf buf) {
        Map<ResourceLocation, Multiblock> multiblocks = buf.readMap(ResourceLocation.STREAM_CODEC, MultiblockSerializer::fromNetwork);
        return new MultiblockSyncS2CPacket(multiblocks);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        MultiblockManager.multiblocks.clear();
        MultiblockManager.multiblocks.putAll(multiblocks);
    }
}