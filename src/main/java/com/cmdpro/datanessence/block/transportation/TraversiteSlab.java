package com.cmdpro.datanessence.block.transportation;

import com.cmdpro.datanessence.api.block.TraversiteBlock;
import net.minecraft.world.level.block.SlabBlock;

public class TraversiteSlab extends SlabBlock implements TraversiteBlock {
    public float boost;
    public TraversiteSlab(Properties pProperties, float boost) {
        super(pProperties);
        this.boost = boost;
    }

    @Override
    public float getBoost() {
        return boost;
    }
}
