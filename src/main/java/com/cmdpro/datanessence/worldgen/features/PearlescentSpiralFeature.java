package com.cmdpro.datanessence.worldgen.features;

import com.cmdpro.datanessence.block.world.PearlescentSpiral;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
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
        if (!pContext.level().getBlockState(origin.below()).is(BlockRegistry.DEEP_SANCTUARY_SAND.get()) || !isValid(pContext.level(), origin)) {
            return false;
        }
        BlockState bottom = BlockRegistry.PEARLESCENT_SPIRAL.get().defaultBlockState().setValue(PearlescentSpiral.PART, PearlescentSpiral.Part.BOTTOM);
        BlockState bottomTop = BlockRegistry.PEARLESCENT_SPIRAL.get().defaultBlockState().setValue(PearlescentSpiral.PART, PearlescentSpiral.Part.BOTTOM_TOP);
        BlockState middle = BlockRegistry.PEARLESCENT_SPIRAL.get().defaultBlockState().setValue(PearlescentSpiral.PART, PearlescentSpiral.Part.MIDDLE);
        BlockState top = BlockRegistry.PEARLESCENT_SPIRAL.get().defaultBlockState().setValue(PearlescentSpiral.PART, PearlescentSpiral.Part.TOP);
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
