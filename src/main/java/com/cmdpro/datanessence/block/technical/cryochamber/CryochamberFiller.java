package com.cmdpro.datanessence.block.technical.cryochamber;

import com.cmdpro.datanessence.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CryochamberFiller extends Block {
    public enum Section implements StringRepresentable {
        MIDDLE("middle"),
        TOP("top");

        private final String name;

        private Section(String name) {
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
    private static final VoxelShape MIDDLE_SHAPE =  Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape TOP_SHAPE =  Block.box(0, 0, 0, 16, 5, 16);
    public static final EnumProperty<Section> SECTION = EnumProperty.create("section", Section.class);
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.getValue(SECTION) == Section.TOP) {
            return TOP_SHAPE;
        }
        return MIDDLE_SHAPE;
    }
    public CryochamberFiller(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SECTION, Section.MIDDLE));
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        if (pState.getValue(SECTION) == Section.MIDDLE) {
            return RenderShape.MODEL;
        } else {
            return RenderShape.INVISIBLE;
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        if (level instanceof ServerLevel serverLevel) {
            if (state.getValue(SECTION) == Section.TOP) {
                if (level.getBlockState(pos.below()).is(BlockRegistry.CRYOCHAMBER_FILLER.get())) {
                    level.destroyBlock(pos.below(), true);
                }
                if (level.getBlockState(pos.below().below()).is(BlockRegistry.CRYOCHAMBER.get())) {
                    level.destroyBlock(pos.below().below(), true);
                }
                serverLevel.setBlockAndUpdate(pos.below().below(), BlockRegistry.AREKKO.get().defaultBlockState());
            }
            if (state.getValue(SECTION) == Section.MIDDLE) {
                if (level.getBlockState(pos.above()).is(BlockRegistry.CRYOCHAMBER_FILLER.get())) {
                    level.destroyBlock(pos.above(), true);
                }
                if (level.getBlockState(pos.below()).is(BlockRegistry.CRYOCHAMBER.get())) {
                    level.destroyBlock(pos.below(), true);
                }
                serverLevel.setBlockAndUpdate(pos.below(), BlockRegistry.AREKKO.get().defaultBlockState());
            }
        }
    }
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!level.getBlockState(pos.below()).is(BlockRegistry.CRYOCHAMBER.get())) {
            return false;
        }
        return super.canSurvive(state, level, pos);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SECTION);
    }
}
