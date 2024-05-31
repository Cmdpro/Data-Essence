package com.cmdpro.datanessence.hiddenblocks;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HiddenBlocksSerializer {
    public HiddenBlock read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("entry")) {
            throw new JsonSyntaxException("Element entry missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("hiddenAs")) {
            throw new JsonSyntaxException("Element hiddenAs missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("originalBlock")) {
            throw new JsonSyntaxException("Element originalBlock missing in entry JSON for " + entryId.toString());
        }
        ResourceLocation entry = ResourceLocation.tryParse(json.get("entry").getAsString());
        BlockState hiddenAs = null;
        Block originalBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(json.get("originalBlock").getAsString()));
        try {
            hiddenAs = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), json.get("hiddenAs").getAsString(), false).blockState();
        } catch (Exception e) {
            DataNEssence.LOGGER.error(e.getMessage());
        }
        return new HiddenBlock(entry, originalBlock, hiddenAs);
    }
    @Nonnull
    public static HiddenBlock fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation entry = buf.readResourceLocation();
        BlockState hiddenAs = null;
        String originalBlockString = buf.readUtf();
        String hiddenAsString = buf.readUtf();
        Block originalBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(originalBlockString));
        try {
            hiddenAs = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), hiddenAsString, false).blockState();
        } catch (Exception e) {
            DataNEssence.LOGGER.error(e.getMessage());
        }
        return new HiddenBlock(entry, originalBlock, hiddenAs);
    }
    public static void toNetwork(FriendlyByteBuf buf, HiddenBlock block) {
        buf.writeResourceLocation(block.entry);
        buf.writeResourceLocation(ForgeRegistries.BLOCKS.getKey(block.originalBlock));
        buf.writeUtf(BlockStateParser.serialize(block.hiddenAs));
    }
}