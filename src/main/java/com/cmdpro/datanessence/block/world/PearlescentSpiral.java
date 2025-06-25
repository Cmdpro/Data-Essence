package com.cmdpro.datanessence.block.world;

import com.cmdpro.databank.megablock.MegablockCoreUtil;
import com.cmdpro.datanessence.registry.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class PearlescentSpiral extends Block implements LiquidBlockContainer {
    public enum Part implements StringRepresentable {
        BOTTOM_TOP("bottom_top"),
        BOTTOM("bottom"),
        MIDDLE("middle"),
        TOP("top");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);


    public PearlescentSpiral(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(PART, Part.BOTTOM));
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getSource(false);
    }
    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockPos pos2 = pos.above();
            while (level.getBlockState(pos2).is(this)) {
                level.destroyBlock(pos2, true);
                pos2 = pos2.above();
            }
            BlockState below = level.getBlockState(pos.below());
            if (below.is(this)) {
                level.setBlockAndUpdate(pos.below(), getStateAtPosition(below, level, pos.below()));
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!level.getBlockState(pos.below()).is(this) && !level.getBlockState(pos.below()).is(TagRegistry.Blocks.SANCTUARY_SAND)) {
            return false;
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        if (fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8) {
            BlockState state = super.getStateForPlacement(context);
            if (state != null) {
                state = getStateAtPosition(state, context.getLevel(), context.getClickedPos());
            }
            return state;
        }
        return null;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(getStateAtPosition(state, level, pos), direction, neighborState, level, pos, neighborPos);
    }

    public BlockState getStateAtPosition(BlockState originalState, LevelAccessor level, BlockPos pos) {
        BlockState state = originalState;
        if (level.getBlockState(pos.below()).is(this)) {
            if (level.getBlockState(pos.above()).is(this)) {
                state = state.setValue(PART, Part.MIDDLE);
            } else {
                state = state.setValue(PART, Part.TOP);
            }
        } else {
            if (level.getBlockState(pos.above()).is(this)) {
                state = state.setValue(PART, Part.BOTTOM);
            } else {
                state = state.setValue(PART, Part.BOTTOM_TOP);
            }
        }
        return state;
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PART);
    }

}
