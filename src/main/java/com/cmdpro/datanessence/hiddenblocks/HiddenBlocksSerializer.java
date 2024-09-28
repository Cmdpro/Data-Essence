package com.cmdpro.datanessence.hiddenblocks;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.datatablet.Page;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HiddenBlocksSerializer {
    public HiddenBlock read(ResourceLocation entryId, JsonObject json) {
        return CODEC.codec().parse(JsonOps.INSTANCE, json).getOrThrow();
    }
    public static final MapCodec<HiddenBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("entry").forGetter(hiddenBlock -> hiddenBlock.entry),
            Block.CODEC.fieldOf("originalBlock").forGetter(hiddenBlock -> hiddenBlock.originalBlock),
            BlockState.CODEC.fieldOf("hiddenAs").forGetter(hiddenBlock -> hiddenBlock.hiddenAs),
            Codec.BOOL.optionalFieldOf("completionRequired", true).forGetter(hiddenBlock -> hiddenBlock.completionRequired)
    ).apply(instance, HiddenBlock::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, HiddenBlock> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceLocation(pValue.entry);
        pBuffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(pValue.originalBlock));
        pBuffer.writeUtf(BlockStateParser.serialize(pValue.hiddenAs));
        pBuffer.writeBoolean(pValue.completionRequired);
    }, pBuffer -> {
        ResourceLocation entry = pBuffer.readResourceLocation();
        BlockState hiddenAs = null;
        String originalBlockString = pBuffer.readUtf();
        String hiddenAsString = pBuffer.readUtf();
        Block originalBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(originalBlockString));
        try {
            hiddenAs = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), hiddenAsString, false).blockState();
        } catch (Exception e) {
            DataNEssence.LOGGER.error(e.getMessage());
        }
        boolean completionRequired = pBuffer.readBoolean();
        return new HiddenBlock(entry, originalBlock, hiddenAs, completionRequired);
    });
}