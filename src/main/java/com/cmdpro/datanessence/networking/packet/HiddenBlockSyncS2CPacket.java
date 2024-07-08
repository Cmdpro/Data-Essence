package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlock;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksSerializer;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.screen.datatablet.EntrySerializer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.nio.charset.MalformedInputException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record HiddenBlockSyncS2CPacket(Map<ResourceLocation, HiddenBlock> blocks) implements Message {
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(blocks, FriendlyByteBuf::writeResourceLocation, HiddenBlocksSerializer::toNetwork);
    }

    public static final ResourceLocation ID = new ResourceLocation(DataNEssence.MOD_ID, "hidden_block_sync");
    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static HiddenBlockSyncS2CPacket read(FriendlyByteBuf buf) {
        Map<ResourceLocation, HiddenBlock> blocks = buf.readMap(FriendlyByteBuf::readResourceLocation, HiddenBlocksSerializer::fromNetwork);
        return new HiddenBlockSyncS2CPacket(blocks);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        HiddenBlocksManager.blocks.clear();
        HiddenBlocksManager.blocks.putAll(blocks);
        ClientDataNEssenceUtil.updateWorld();
    }
}