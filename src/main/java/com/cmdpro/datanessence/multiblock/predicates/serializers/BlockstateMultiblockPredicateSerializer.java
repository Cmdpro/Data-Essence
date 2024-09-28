package com.cmdpro.datanessence.multiblock.predicates.serializers;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.datanessence.multiblock.predicates.BlockstateMultiblockPredicate;
import com.cmdpro.datanessence.screen.datatablet.pages.MultiblockPage;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockstateMultiblockPredicateSerializer extends MultiblockPredicateSerializer<BlockstateMultiblockPredicate> {

    public static BlockState getBlockStateFromBuf(FriendlyByteBuf buf) {
        try {
            return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), buf.readUtf(), false).blockState();
        } catch (Exception e) {
            DataNEssence.LOGGER.error(e.getMessage());
            return Blocks.AIR.defaultBlockState();
        }
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockstateMultiblockPredicate> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeUtf(BlockStateParser.serialize(pValue.self));
    }, (pBuffer) -> {
        BlockState state = getBlockStateFromBuf(pBuffer);
        return new BlockstateMultiblockPredicate(state);
    });
    public static final MapCodec<BlockstateMultiblockPredicate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            BlockState.CODEC.fieldOf("state").forGetter(page -> page.self)
    ).apply(instance, BlockstateMultiblockPredicate::new));
    @Override
    public MapCodec<BlockstateMultiblockPredicate> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BlockstateMultiblockPredicate> getStreamCodec() {
        return STREAM_CODEC;
    }
}
