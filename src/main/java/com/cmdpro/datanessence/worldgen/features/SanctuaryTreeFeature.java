package com.cmdpro.datanessence.worldgen.features;

import com.cmdpro.datanessence.registry.BlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SanctuaryTreeFeature extends Feature<NoneFeatureConfiguration> {
    public SanctuaryTreeFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        Vec3i offset = new Vec3i(8+pContext.random().nextIntBetweenInclusive(-8, 8), 0, 8+pContext.random().nextIntBetweenInclusive(-8, 8));
        float height = pContext.random().nextIntBetweenInclusive(12, 16);
        BlockPos origin = pContext.level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pContext.origin().offset(offset));
        if (!pContext.level().getBlockState(origin.below()).getFluidState().isEmpty()) {
            return false;
        }
        BlockPos center = origin;
        int yRot = pContext.random().nextIntBetweenInclusive(-180, 180);
        Vec3 change = calculateViewVector(pContext.random().nextIntBetweenInclusive(-45, -35), yRot);
        List<BlockPos> leavesPoints = new ArrayList<>();
        for (int y = 0; y <= height+1; y++) {
            float logSize = 1f;
            for (int x2 = (int) -2; x2 <= 2; x2++) {
                for (int y2 = (int) -2; y2 <= 2; y2++) {
                    for (int z2 = (int) -2; z2 <= 2; z2++) {
                        if (new Vec3(x2, y2, z2).distanceTo(new Vec3(0, 0, 0)) <= logSize) {
                            BlockPos pos = center.offset(x2, y2, z2);
                            pos = pos.offset((int)(change.x*y), (int)(change.y*y), (int)(change.z*y));
                            if (pContext.level().getBlockState(pos).canBeReplaced() || pContext.level().getBlockState(pos).is(BlockRegistry.CRYSTALLINE_LEAVES.get())) {
                                setBlock(pContext.level(), pos, BlockRegistry.CRYSTALLINE_LOG.get().defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
        leavesPoints.add(center.offset((int)(change.x*height), (int)(change.y*height), (int)(change.z*height)));
        Vec3 newChange = calculateViewVector(pContext.random().nextIntBetweenInclusive(-60, -50), 180-yRot);
        float mult = height/2;
        for (int i = 0; i < 2; i++) {
            float newHeight = pContext.random().nextIntBetweenInclusive(8, 12);
            center = origin.offset((int)(change.x*mult), (int)(change.y*mult), (int)(change.z*mult));
            for (int y = 0; y <= newHeight; y++) {
                float logSize = 1f;
                for (int x2 = (int) -2; x2 <= 2; x2++) {
                    for (int y2 = (int) -2; y2 <= 2; y2++) {
                        for (int z2 = (int) -2; z2 <= 2; z2++) {
                            if (new Vec3(x2, y2, z2).distanceTo(new Vec3(0, 0, 0)) <= logSize) {
                                BlockPos pos = center.offset(x2, y2, z2);
                                pos = pos.offset((int) (newChange.x * y), (int) (newChange.y * y), (int) (newChange.z * y));
                                if (pContext.level().getBlockState(pos).canBeReplaced() || pContext.level().getBlockState(pos).is(BlockRegistry.CRYSTALLINE_LEAVES.get())) {
                                    setBlock(pContext.level(), pos, BlockRegistry.CRYSTALLINE_LOG.get().defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
            leavesPoints.add(center.offset((int) (newChange.x * newHeight), (int) (newChange.y * newHeight), (int) (newChange.z * newHeight)));
            mult += height/2;
        }
        BlockPos highestPos = null;
        BlockPos lowestPos = null;
        int minX = center.getX();
        int minZ = center.getZ();
        int maxX = center.getX();
        int maxZ = center.getZ();
        for (BlockPos i : leavesPoints) {
            if (highestPos == null || highestPos.getY() < i.getY()) {
                highestPos = i;
            }
            if (lowestPos == null || lowestPos.getY() > i.getY()) {
                lowestPos = i;
            }
            if (i.getX() < minX) {
                minX = i.getX();
            }
            if (i.getZ() < minZ) {
                minZ = i.getZ();
            }
            if (i.getX() > maxX) {
                maxX = i.getX();
            }
            if (i.getZ() > maxZ) {
                maxZ = i.getZ();
            }
        }
        highestPos = highestPos.offset(0, 10, 0);
        BlockPos middlePos = new BlockPos(minX + ((maxX-minX)/2), lowestPos.getY()+((highestPos.getY()-lowestPos.getY())/2), minZ + ((maxZ-minZ)/2));
        double largestDistanceFromCenter = 0;
        for (BlockPos i : leavesPoints) {
            if (i.getCenter().multiply(1, 0, 1).distanceTo(middlePos.getCenter().multiply(1, 0, 1)) > largestDistanceFromCenter) {
                largestDistanceFromCenter = i.getCenter().multiply(1, 0, 1).distanceTo(middlePos.getCenter().multiply(1, 0, 1));
            }
        }
        largestDistanceFromCenter += 4;
        float curveHeight = highestPos.getY()-lowestPos.getY();
        //PLEASE PUT THE LEAVES CODE HERE
        return true;
    }
    public final Vec3 calculateViewVector(float pXRot, float pYRot) {
        float f = pXRot * (float) (Math.PI / 180.0);
        float f1 = -pYRot * (float) (Math.PI / 180.0);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }
}
