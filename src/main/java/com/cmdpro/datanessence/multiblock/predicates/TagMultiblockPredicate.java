package com.cmdpro.datanessence.multiblock.predicates;

import com.cmdpro.datanessence.multiblock.MultiblockPredicate;
import com.cmdpro.datanessence.multiblock.MultiblockPredicateSerializer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TagMultiblockPredicate extends MultiblockPredicate {
    public TagMultiblockPredicate(TagKey<Block> tag) {
        this.tag = tag;
    }
    public TagKey<Block> tag;
    @Override
    public boolean isSame(BlockState other) {
        return other.is(tag);
    }

    @Override
    public MultiblockPredicateSerializer getSerializer() {
        return null;
    }
}
