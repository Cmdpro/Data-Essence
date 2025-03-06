package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.block.TraversiteBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TraversiteStairs extends StairBlock implements TraversiteBlock {
    public float boost;
    public TraversiteStairs(BlockState state, Properties pProperties, float boost) {
        super(state, pProperties);
        this.boost = boost;
    }

    @Override
    public float getBoost() {
        return boost;
    }
}
