package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlock;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksSerializer;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.screen.datatablet.EntrySerializer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.MalformedInputException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class HiddenBlockSyncS2CPacket {
    private final Map<ResourceLocation, HiddenBlock> blocks;

    public HiddenBlockSyncS2CPacket(Map<ResourceLocation, HiddenBlock> blocks) {
        this.blocks = blocks;
    }

    public HiddenBlockSyncS2CPacket(FriendlyByteBuf buf) {
        this.blocks = buf.readMap(FriendlyByteBuf::readResourceLocation, HiddenBlocksSerializer::fromNetwork);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(blocks, FriendlyByteBuf::writeResourceLocation, HiddenBlocksSerializer::toNetwork);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientPacketHandler.handlePacket(this, supplier);
        });
        return true;
    }
    public static class ClientPacketHandler {
        public static void handlePacket(HiddenBlockSyncS2CPacket msg, Supplier<NetworkEvent.Context> supplier) {
            HiddenBlocksManager.blocks.clear();
            for (Map.Entry<ResourceLocation, HiddenBlock> i : msg.blocks.entrySet()) {
                HiddenBlocksManager.blocks.put(i.getKey(), i.getValue());
            }
            for (ChunkRenderDispatcher.RenderChunk i : Minecraft.getInstance().levelRenderer.viewArea.chunks) {
                i.setDirty(false);
            }
        }
    }
}