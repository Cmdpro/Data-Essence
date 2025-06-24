package com.cmdpro.datanessence.worldgen.features;

import com.cmdpro.datanessence.block.world.PearlescentSpiral;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.TagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PearlescentSpiralFeature extends Feature<NoneFeatureConfiguration> {
    public PearlescentSpiralFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        int height = (int)(25f*(Mth.nextFloat(pContext.random(), 0.5f, 1.5f)));
        BlockPos origin = pContext.origin();
        if (!pContext.level().getBlockState(origin.below()).is(TagRegistry.Blocks.SANCTUARY_SAND) || !isValid(pContext.level(), origin)) {
            return false;
        }
        BlockState[] potentialStates = new BlockState[] {
                BlockRegistry.CYAN_PEARLESCENT_SPIRAL.get().defaultBlockState(),
                BlockRegistry.MAGENTA_PEARLESCENT_SPIRAL.get().defaultBlockState(),
                BlockRegistry.YELLOW_PEARLESCENT_SPIRAL.get().defaultBlockState()
        };
        BlockState defaultState = potentialStates[Mth.nextInt(pContext.random(), 0, potentialStates.length-1)];
        BlockState bottom = defaultState.setValue(PearlescentSpiral.PART, PearlescentSpiral.Part.BOTTOM);
        BlockState bottomTop = defaultState.setValue(PearlescentSpiral.PART, PearlescentSpiral.Part.BOTTOM_TOP);
        BlockState middle = defaultState.setValue(PearlescentSpiral.PART, PearlescentSpiral.Part.MIDDLE);
        BlockState top = defaultState.setValue(PearlescentSpiral.PART, PearlescentSpiral.Part.TOP);
        for (int i = 0; i <= height; i++) {
            BlockPos pos = origin.offset(0, i, 0);
            BlockState state = middle;
            if (i == 0) {
                state = bottom;
                if (!isValid(pContext.level(), pos.above())) {
                    state = bottomTop;
                }
            } else if (i == height) {
                state = top;
            } else if (!isValid(pContext.level(), pos.above())) {
                state = top;
            }
            setBlock(pContext.level(), pos, state);
            if (state == top || state == bottomTop) {
                break;
            }
        }
        return true;
    }
    public boolean isValid(WorldGenLevel level, BlockPos pos) {
        return !level.getFluidState(pos).isEmpty();
    }
}
