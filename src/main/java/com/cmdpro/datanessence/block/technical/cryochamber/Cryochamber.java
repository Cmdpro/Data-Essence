package com.cmdpro.datanessence.block.technical.cryochamber;

import com.cmdpro.datanessence.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class Cryochamber extends Block implements EntityBlock {
    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    public Cryochamber(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!context.getLevel().getBlockState(context.getClickedPos().above()).canBeReplaced(context)) {
            return null;
        }
        if (!context.getLevel().getBlockState(context.getClickedPos().above().above()).canBeReplaced(context)) {
            return null;
        }
        return super.getStateForPlacement(context);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!level.getBlockState(pos.above()).is(BlockRegistry.CRYOCHAMBER_FILLER.get())) {
            return false;
        }
        if (!level.getBlockState(pos.above().above()).is(BlockRegistry.CRYOCHAMBER_FILLER.get())) {
            return false;
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (level.getBlockState(pos.above()).canBeReplaced()) {
            level.setBlockAndUpdate(pos.above(), BlockRegistry.CRYOCHAMBER_FILLER.get().defaultBlockState());
            level.setBlockAndUpdate(pos.above().above(), BlockRegistry.CRYOCHAMBER_FILLER.get().defaultBlockState().setValue(CryochamberFiller.SECTION, CryochamberFiller.Section.TOP));
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        if (level instanceof ServerLevel serverLevel) {
            if (level.getBlockState(pos.above()).is(BlockRegistry.CRYOCHAMBER_FILLER.get())) {
                level.destroyBlock(pos.above(), true);
            }
            if (level.getBlockState(pos.above().above()).is(BlockRegistry.CRYOCHAMBER_FILLER.get())) {
                level.destroyBlock(pos.above().above(), true);
            }
            serverLevel.setBlockAndUpdate(pos, BlockRegistry.AREKKO.get().defaultBlockState());
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CryochamberBlockEntity(pos, state);
    }
}
