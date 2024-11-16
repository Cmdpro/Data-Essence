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
        Vec3i offset = new Vec3i(8+pContext.random().nextIntBetweenInclusive(-4, 4), 0, 8+pContext.random().nextIntBetweenInclusive(-8, 8));
        float sizeMult = pContext.random().nextFloat()+0.5f;
        float height = 20*sizeMult;
        BlockPos origin = pContext.level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pContext.origin().offset(offset));
        if (!pContext.level().getBlockState(origin.below()).getFluidState().isEmpty()) {
            return false;
        }
        BlockPos center = origin;
        for (int y = 0; y <= height; y++) {
            /*
            if (y % 4 == 0 && y > 1 && y < height-4) {
                branch(pContext, center, sizeMult);
            }
            */
            float range = 2*sizeMult;
            if (y >= height-2) {
                range -= (2f-(y-(height-2f)))*sizeMult;
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
            if (y % 4 == 0 && y > 1 && y < height-4) {
                center = center.offset(pContext.random().nextIntBetweenInclusive(-1, 1), 0, pContext.random().nextIntBetweenInclusive(-1, 1));
            }
        }
        for (int y = 0; y <= 10; y++) {
            BlockPos center2 = center.offset(0, y-2, 0);
            float leavesRange = 6f*(1f-((float)y/10f));
            for (int x = (int)-leavesRange; x <= (int)leavesRange; x++) {
                for (int z = (int)-leavesRange; z <= (int)leavesRange; z++) {
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
        return true;
    }
    public void branch(FeaturePlaceContext<NoneFeatureConfiguration> pContext, BlockPos start, float sizeMult) {
        float size = (5f * ((pContext.random().nextFloat()/2f) + 0.75f));
        size *= sizeMult;
        size += 2*sizeMult;
        float direction = pContext.random().nextFloat() * 360f;
        for (int i = 0; i <= size; i++) {
            float range = (2*(1f-(i/size)))*sizeMult;
            BlockPos posCenter = start.offset(new Vec3i((int) (Math.cos(Math.toRadians(direction)) * i), i/3, (int) (Math.sin(Math.toRadians(direction)) * i)));
            Direction dir = Direction.fromYRot(direction);
            for (int x = (int)-range; x <= (int)range; x++) {
                for (int y = (int)-range; y <= (int)range; y++) {
                    for (int z = (int)-range; z <= (int)range; z++) {
                        BlockPos pos = posCenter.offset(new Vec3i(x, y, z));
                        double dist = posCenter.getCenter().distanceTo(pos.getCenter());
                        if (dist <= range) {
                            setBlock(pContext.level(), pos, Blocks.OAK_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, dir.getAxis()));
                        }
                    }
                }
            }
        }
    }
}
