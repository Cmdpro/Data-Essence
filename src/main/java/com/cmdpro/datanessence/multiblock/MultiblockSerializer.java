package com.cmdpro.datanessence.multiblock;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlock;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiblockSerializer {
    public Multiblock read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("key")) {
            throw new JsonSyntaxException("Element key missing in multiblock JSON for " + entryId.toString());
        }
        if (!json.has("layers")) {
            throw new JsonSyntaxException("Element multiblockLayers missing in multiblock JSON for " + entryId.toString());
        }
        if (!json.has("offset")) {
            throw new JsonSyntaxException("Element offset missing in multiblock JSON for " + entryId.toString());
        }
        HashMap<Character, BlockState> key = new HashMap<>();
        try {
            for (Map.Entry<String, JsonElement> i : json.getAsJsonObject("key").asMap().entrySet()) {
                key.put(i.getKey().charAt(0), BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), i.getValue().getAsString(), false).blockState());
            }
        } catch (Exception e) {
            DataNEssence.LOGGER.error(e.getMessage());
        }
        List<String[]> layers = new ArrayList<>();
        for (JsonElement i : json.getAsJsonArray("layers")) {
            List<String> layer = new ArrayList<>();
            for (JsonElement o : i.getAsJsonArray()) {
                layer.add(o.getAsString());
            }
            layers.add(layer.toArray(new String[0]));
        }
        String[][] multiblockLayers = layers.toArray(new String[0][]);
        BlockPos offset = new BlockPos(
                json.getAsJsonObject("offset").get("x").getAsInt(),
                json.getAsJsonObject("offset").get("y").getAsInt(),
                json.getAsJsonObject("offset").get("z").getAsInt()
        );
        return new Multiblock(multiblockLayers, key, offset);
    }
    @Nonnull
    public static Multiblock fromNetwork(FriendlyByteBuf buf) {
        HashMap<Character, BlockState> key = new HashMap<>(buf.readMap((a) -> a.readChar(), (a) -> {
            try {
                return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), a.readUtf(), false).blockState();
            } catch (Exception e) {
                DataNEssence.LOGGER.error(e.getMessage());
                return Blocks.AIR.defaultBlockState();
            }
        }));
        BlockPos offset = buf.readBlockPos();
        List<String[]> layers = buf.readList((a) -> a.readList(FriendlyByteBuf::readUtf).toArray(new String[0]));
        return new Multiblock(layers.toArray(new String[0][]), key, offset);
    }
    public static void toNetwork(FriendlyByteBuf buf, Multiblock multiblock) {
        buf.writeMap(multiblock.key, (a, b) -> a.writeChar(b), (a, b) -> {
            buf.writeUtf(BlockStateParser.serialize(b));
        });
        buf.writeBlockPos(multiblock.offset);
        List<List<String>> layers = new ArrayList<>();
        for (String[] i : multiblock.multiblockLayers) {
            layers.add(List.of(i));
        }
        buf.writeCollection(layers, (a, b) -> {
            a.writeCollection(b, FriendlyByteBuf::writeUtf);
        });
    }
}