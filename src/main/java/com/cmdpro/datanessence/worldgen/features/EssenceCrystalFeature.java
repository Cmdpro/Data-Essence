package com.cmdpro.datanessence.worldgen.features;

import com.cmdpro.datanessence.block.EssenceCrystal;
import com.cmdpro.datanessence.init.BlockInit;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EssenceCrystalFeature extends Feature<NoneFeatureConfiguration> {
    public EssenceCrystalFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        boolean reachedBottomOrTop = false;
        int yChange = -1;
        if (pContext.random().nextBoolean()) {
            yChange = 1;
        }
        BlockPos pos = pContext.origin();
        if (!pContext.level().isEmptyBlock(pos)) {
            return false;
        }
        for (int i = 0; i < 64; i += 1) {
            if (pContext.level().getBlockState(pContext.origin().offset(0, i*yChange, 0)).isSolid()) {
                reachedBottomOrTop = true;
                pos = pContext.origin().offset(0, i+(-yChange), 0);
                break;
            }
        }
        if (!reachedBottomOrTop) {
            return false;
        }
        int amount = pContext.random().nextInt(2, 4);
        for (int i = 0; i < amount; i++) {
            int xOffset = 0;
            int yOffset = 0;
            int zOffset = 0;
            Direction dir = Direction.DOWN;
            boolean foundValid = false;
            for (int o = 0; o <= 10; o++) {
                xOffset = pContext.random().nextIntBetweenInclusive(-2, 2);
                yOffset = pContext.random().nextIntBetweenInclusive(-2, 2);
                zOffset = pContext.random().nextIntBetweenInclusive(-2, 2);
                BlockPos offset = pos.offset(xOffset, yOffset, zOffset);
                BlockState state = pContext.level().getBlockState(offset);
                if (state.isAir()) {
                    Direction[] directions = Direction.allShuffled(pContext.random()).toArray(new Direction[0]);
                    for (Direction p : directions) {
                        if (pContext.level().getBlockState(offset.relative(p)).isCollisionShapeFullBlock(pContext.level(), offset.relative(p))) {
                            dir = p.getOpposite();
                            foundValid = true;
                            break;
                        }
                    }
                    if (foundValid) {
                        break;
                    }
                }
            }
            if (foundValid) {
                setBlock(pContext.level(), pos.offset(xOffset, yOffset, zOffset), BlockInit.ESSENCECRYSTAL.get().defaultBlockState().setValue(EssenceCrystal.FACING, dir));
            }
        }
        return true;
    }
}
