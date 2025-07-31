package com.cmdpro.datanessence.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class SpreadingPlant extends Block {
    public int spreadChance; // how often this plant grows by spreading, like mushrooms
    public int plantLimit; // how many plants can be in a 9x3x9 volume around the original plant before it will stop spreading

    public SpreadingPlant(Properties properties, int spreadChance, int plantLimit) {
        super(properties);
        this.spreadChance = spreadChance;
        this.plantLimit = plantLimit;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        grow(state, world, pos, random, spreadChance);
    }

    /**
     * Trigger a mushroom-like growth in this plant.
     * @param state block state to operate on
     * @param world world to operate in
     * @param pos coords of the block
     * @param random random source
     * @param chance how often the growth may occur (works inverse of what one may intuit - smaller number is more frequent!)
     */
    public void grow(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, int chance) {
        if (random.nextInt(chance) == 0) {

            for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, -1, -4), pos.offset(4, 1, 4))) {
                if (world.getBlockState(blockpos).is(this)) {
                    if (--plantLimit <= 0) {
                        return;
                    }
                }
            }

            BlockPos blockpos1 = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int k = 0; k < 4; k++) {
                if (world.isEmptyBlock(blockpos1) && state.canSurvive(world, blockpos1)) {
                    pos = blockpos1;
                }

                blockpos1 = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }

            if (world.isEmptyBlock(blockpos1) && state.canSurvive(world, blockpos1)) {
                world.setBlock(blockpos1, state, 2);
            }
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState pState) {
        return true;
    }
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(BlockTags.DIRT) || pState.getBlock() instanceof net.minecraft.world.level.block.FarmBlock;
    }
    @Override
    protected BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return !pState.canSurvive(pLevel, pCurrentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState belowBlockState = pLevel.getBlockState(blockpos);
        net.neoforged.neoforge.common.util.TriState soilDecision = belowBlockState.canSustainPlant(pLevel, blockpos, Direction.UP, pState);
        if (!soilDecision.isDefault()) return soilDecision.isTrue();
        return this.mayPlaceOn(belowBlockState, pLevel, blockpos);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return pState.getFluidState().isEmpty();
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
        return pPathComputationType == PathComputationType.AIR && !this.hasCollision || super.isPathfindable(pState, pPathComputationType);
    }
}
