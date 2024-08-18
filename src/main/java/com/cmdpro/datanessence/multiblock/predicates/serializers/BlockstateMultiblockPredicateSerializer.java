package com.cmdpro.datanessence.multiblock.predicates.serializers;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.multiblock.MultiblockPredicate;
import com.cmdpro.datanessence.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.datanessence.multiblock.predicates.BlockstateMultiblockPredicate;
import com.google.gson.JsonObject;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockstateMultiblockPredicateSerializer extends MultiblockPredicateSerializer<BlockstateMultiblockPredicate> {
    @Override
    public BlockstateMultiblockPredicate fromNetwork(FriendlyByteBuf buf) {
        BlockState state = getBlockStateFromBuf(buf);
        return new BlockstateMultiblockPredicate(state);
    }

    public BlockState getBlockStateFromBuf(FriendlyByteBuf buf) {
        try {
            return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), buf.readUtf(), false).blockState();
        } catch (Exception e) {
            DataNEssence.LOGGER.error(e.getMessage());
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, BlockstateMultiblockPredicate predicate) {
        buf.writeUtf(BlockStateParser.serialize(predicate.self));
    }

    @Override
    public BlockstateMultiblockPredicate fromJson(JsonObject obj) {
        try {
            return new BlockstateMultiblockPredicate(BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), obj.get("state").getAsString(), false).blockState());
        } catch (Exception e) {
            return new BlockstateMultiblockPredicate(Blocks.AIR.defaultBlockState());
        }
    }
}
