package com.cmdpro.datanessence.block.auxiliary;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FluidBottler extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public FluidBottler(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    private static final VoxelShape SHAPE_FALLOFF =  Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_WEST =  Shapes.or(
            Block.box(2, 0, 2, 10, 2, 14),
            Block.box(10, 0, 2, 16, 16, 14),
            Block.box(4, 11, 4, 10, 16, 12)
    );
    private static final VoxelShape SHAPE_EAST =  Shapes.or(
            Block.box(6, 0, 2, 14, 2, 14),
            Block.box(0, 0, 2, 6, 16, 14),
            Block.box(6, 11, 4, 12, 16, 12)
    );
    private static final VoxelShape SHAPE_SOUTH =  Shapes.or(
            Block.box(2, 0, 6, 14, 2, 14),
            Block.box(2, 0, 0, 14, 16, 6),
            Block.box(4, 11, 6, 12, 16, 12)
    );
    private static final VoxelShape SHAPE_NORTH =  Shapes.or(
            Block.box(2, 0, 2, 14, 2, 14),
            Block.box(2, 0, 10, 14, 16, 16),
            Block.box(4, 11, 4, 12, 16, 10)
    );
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            default -> SHAPE_FALLOFF;
        };
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        FluidBottlerBlockEntity ent = new FluidBottlerBlockEntity(pPos, pState);
        return ent;
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof FluidBottlerBlockEntity) {
                ((FluidBottlerBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof FluidBottlerBlockEntity ent) {
                pPlayer.openMenu(ent, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof FluidBottlerBlockEntity ent) {
                FluidBottlerBlockEntity.tick(lvl, pos, st, ent);
            }
        };
    }
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }
}
