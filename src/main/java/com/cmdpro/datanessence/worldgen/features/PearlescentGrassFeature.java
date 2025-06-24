package com.cmdpro.datanessence.worldgen.features;

import com.cmdpro.datanessence.registry.BlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PearlescentGrassFeature extends Feature<NoneFeatureConfiguration> {
    public PearlescentGrassFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        BlockState[] potentialStates = new BlockState[] {
                BlockRegistry.CYAN_PEARLESCENT_GRASS.get().defaultBlockState(),
                BlockRegistry.MAGENTA_PEARLESCENT_GRASS.get().defaultBlockState(),
                BlockRegistry.YELLOW_PEARLESCENT_GRASS.get().defaultBlockState()
        };
        BlockState defaultState = potentialStates[Mth.nextInt(pContext.random(), 0, potentialStates.length-1)];
        boolean flag = false;
        RandomSource randomsource = pContext.random();
        WorldGenLevel worldgenlevel = pContext.level();
        BlockPos blockpos = pContext.origin();
        int i = randomsource.nextInt(8) - randomsource.nextInt(8);
        int j = randomsource.nextInt(8) - randomsource.nextInt(8);
        int k = worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR, blockpos.getX() + i, blockpos.getZ() + j);
        BlockPos blockpos1 = new BlockPos(blockpos.getX() + i, k, blockpos.getZ() + j);
        if (worldgenlevel.getBlockState(blockpos1).is(Blocks.WATER)) {
            if (defaultState.canSurvive(worldgenlevel, blockpos1)) {
                worldgenlevel.setBlock(blockpos1, defaultState, 2);
                flag = true;
            }
        }

        return flag;
    }
}
