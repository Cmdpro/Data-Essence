package com.cmdpro.datanessence.hiddenblocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class HiddenBlock {
    public HiddenBlock(ResourceLocation entry, Block originalBlock, BlockState hiddenAs, boolean completionRequired) {
        this.entry = entry;
        this.originalBlock = originalBlock;
        this.hiddenAs = hiddenAs;
        this.completionRequired = completionRequired;
    }
    public ResourceLocation entry;
    public Block originalBlock;
    public BlockState hiddenAs;
    public boolean completionRequired;
}
