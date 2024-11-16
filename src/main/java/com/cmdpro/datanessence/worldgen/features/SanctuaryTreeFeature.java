package com.cmdpro.datanessence.worldgen.features;

import com.cmdpro.datanessence.registry.BlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SanctuaryTreeFeature extends Feature<NoneFeatureConfiguration> {
    public SanctuaryTreeFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        Vec3i offset = new Vec3i(8+pContext.random().nextIntBetweenInclusive(-8, 8), 0, 8+pContext.random().nextIntBetweenInclusive(-8, 8));
        float sizeMult = pContext.random().nextFloat()+0.5f;
        float height = 10*sizeMult;
        int extra = 6;
        height += extra;
        BlockPos origin = pContext.level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pContext.origin().offset(offset));
        if (!pContext.level().getBlockState(origin.below()).getFluidState().isEmpty()) {
            return false;
        }
        BlockPos center = origin;
        for (int y = 0; y <= height; y++) {
            float range = 2*Math.clamp(sizeMult, 0.5f, 1f);
            if (y >= (height-extra)-2) {
                range -= 1*sizeMult;
            }
            for (int x = (int)-range; x <= (int)range; x++) {
                for (int z = (int)-range; z <= (int)range; z++) {
                    BlockPos pos = center.offset(new Vec3i(x, 0, z));
                    double dist = center.getCenter().distanceTo(pos.getCenter());
                    if (dist <= range) {
                        setBlock(pContext.level(), pos, Blocks.OAK_LOG.defaultBlockState());
                    }
                }
            }
            center = center.offset(0, 1, 0);
            if (y % 4 == 0 && y > 1 && y < (height-4)-extra) {
                center = center.offset(pContext.random().nextIntBetweenInclusive(-1, 1), 0, pContext.random().nextIntBetweenInclusive(-1, 1));
            }
        }
        for (int y2 = 0; y2 <= 5; y2 += 5) {
            for (int y = 0; y <= 10; y++) {
                BlockPos center2 = center.offset(0, ((y - 4)+y2)-extra, 0);
                float leavesRange = 4f * (1f - ((float) y / 10f));
                for (int x = (int) -leavesRange; x <= (int) leavesRange; x++) {
                    for (int z = (int) -leavesRange; z <= (int) leavesRange; z++) {
                        BlockPos pos = center2.offset(new Vec3i(x, 0, z));
                        double dist = center2.getCenter().distanceTo(pos.getCenter());
                        if (dist <= leavesRange) {
                            if (pContext.level().getBlockState(pos).canBeReplaced()) {
                                setBlock(pContext.level(), pos, Blocks.OAK_LEAVES.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
