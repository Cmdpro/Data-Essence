package com.cmdpro.datanessence.block.generation;

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

public class IndustrialPlantSiphon extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public IndustrialPlantSiphon(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

//    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 16, 16);

    private static final VoxelShape SHAPE_NORTH =  Shapes.or(
            //bottom
            Block.box(0, 0, 0, 16, 6, 16),

            //left
            Block.box(0, 6, 2, 2, 14, 14),

            //right
            Block.box(14, 6, 2, 16, 14, 14),

            //back
            Block.box(0, 6, 14, 16, 16, 16)

    );
    private static final VoxelShape SHAPE_SOUTH =  Shapes.or(
            //bottom
            Block.box(0, 0, 0, 16, 6, 16),

            //left
            Block.box(0, 6, 2, 2, 14, 14),

            //right
            Block.box(14, 6, 2, 16, 14, 14),

            //back
            Block.box(0, 6, 0, 16, 16, 2)

    );
    private static final VoxelShape SHAPE_EAST =  Shapes.or(
            //bottom
            Block.box(0, 0, 0, 16, 6, 16),

            //left
            Block.box(2, 6, 0, 14, 14, 2),

            //right
            Block.box(2, 6, 14, 14, 14, 16),

            //back
            Block.box(0, 6, 0, 2, 16, 16)

    );
    private static final VoxelShape SHAPE_WEST =  Shapes.or(
            //bottom
            Block.box(0, 0, 0, 16, 6, 16),

            //left
            Block.box(2, 6, 0, 14, 14, 2),

            //right
            Block.box(2, 6, 14, 14, 14, 16),

            //back
            Block.box(14, 6, 0, 16, 16, 16)

    );
    private static final VoxelShape SHAPE_FALLOFF = Block.box(0,0,0,16,16,16);

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
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        IndustrialPlantSiphonBlockEntity ent = new IndustrialPlantSiphonBlockEntity(pPos, pState);
        return ent;
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof IndustrialPlantSiphonBlockEntity ent) {
                ent.drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof IndustrialPlantSiphonBlockEntity ent) {
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
            if (blockEntity instanceof IndustrialPlantSiphonBlockEntity ent) {
                IndustrialPlantSiphonBlockEntity.tick(lvl, pos, st, ent);
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
