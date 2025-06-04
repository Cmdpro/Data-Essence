package com.cmdpro.datanessence.block.transportation;

import com.cmdpro.datanessence.api.block.TraversiteBlock;
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
