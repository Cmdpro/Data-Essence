package com.cmdpro.datanessence.multiblock.predicates;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.multiblock.MultiblockPredicate;
import com.cmdpro.datanessence.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.datanessence.registry.MultiblockPredicateRegistry;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Optional;

public class TagMultiblockPredicate extends MultiblockPredicate {
    public TagMultiblockPredicate(TagKey<Block> tag) {
        this.tag = tag;
    }
    public TagKey<Block> tag;
    @Override
    public boolean isSame(BlockState other, Rotation rotation) {
        return other.is(tag);
    }

    @Override
    public MultiblockPredicateSerializer getSerializer() {
        return MultiblockPredicateRegistry.TAG.get();
    }
    @Override
    public BlockState getVisual() {
        HolderSet.Named<Block> tag = BuiltInRegistries.BLOCK.getOrCreateTag(this.tag);
        List<Holder<Block>> blocks = tag.stream().toList();
        if (!blocks.isEmpty()) {
            Block block = blocks.get((int)(Util.getMillis() / 1000L) % blocks.size()).value();
            return block.defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }
}
