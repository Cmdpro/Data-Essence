package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public abstract class BaseEssencePoint extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    protected static final VoxelShape NORTH_AABB = Block.box(4.0D, 4.0D, 12.0D, 12.0D, 12.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 4.0D);
    protected static final VoxelShape WEST_AABB = Block.box(12.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 4.0D, 4.0D, 4.0D, 12.0D, 12.0D);
    protected static final VoxelShape UP_AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D);
    protected static final VoxelShape DOWN_AABB = Block.box(4.0D, 12.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public BaseEssencePoint(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.FLOOR));
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACE, FACING);
    }
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch ((AttachFace)pState.getValue(FACE)) {
            case FLOOR:
                return UP_AABB;
            case WALL:
                switch ((Direction)pState.getValue(FACING)) {
                    case EAST:
                        return EAST_AABB;
                    case WEST:
                        return WEST_AABB;
                    case SOUTH:
                        return SOUTH_AABB;
                    case NORTH:
                    default:
                        return NORTH_AABB;
                }
            default:
                return DOWN_AABB;
        }
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        for(Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, pContext.getHorizontalDirection());
            } else {
                blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction.getOpposite());
            }

            if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
                return blockstate;
            }
        }

        return null;
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return canAttach(pLevel, pPos, getConnectedDirection(pState).getOpposite());
    }
    public static Direction getConnectedDirection(BlockState pState) {
        switch ((AttachFace)pState.getValue(FACE)) {
            case CEILING:
                return Direction.DOWN;
            case FLOOR:
                return Direction.UP;
            default:
                return pState.getValue(FACING);
        }
    }
    public static boolean canAttach(LevelReader pReader, BlockPos pPos, Direction pDirection) {
        BlockPos blockpos = pPos.relative(pDirection);
        return pReader.getBlockState(blockpos).isFaceSturdy(pReader, blockpos, pDirection.getOpposite());
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.getBlockEntity(pPos) instanceof BaseEssencePointBlockEntity ent) {
            if (ent.link != null) {
                ItemEntity item = new ItemEntity(pLevel, pPos.getCenter().x, pPos.getCenter().y, pPos.getCenter().z, new ItemStack(getRequiredWire()));
                pLevel.addFreshEntity(item);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return getConnectedDirection(pState).getOpposite() == pFacing && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }
    public abstract Item getRequiredWire();
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof BaseEssencePointBlockEntity ent) {
                if (pPlayer.isHolding(getRequiredWire())) {
                    Optional<BlockEntity> linkFrom = pPlayer.getData(AttachmentTypeRegistry.LINK_FROM);
                    if (!linkFrom.isPresent()) {
                        if (ent.link == null) {
                            pPlayer.setData(AttachmentTypeRegistry.LINK_FROM, Optional.of(ent));
                            DataNEssenceUtil.updatePlayerData(pPlayer);
                        }
                    } else {
                        if (linkFrom.get().getBlockState().getBlock() instanceof BaseEssencePoint other) {
                            if (other.getRequiredWire() == getRequiredWire() && ent != linkFrom.get() && (ent.link == null || !ent.link.equals(linkFrom.get().getBlockPos()))) {
                                if (linkFrom.get() instanceof BaseEssencePointBlockEntity linkFrom2) {
                                    linkFrom2.link = pPos;
                                    linkFrom2.updateBlock();
                                    pPlayer.setData(AttachmentTypeRegistry.LINK_FROM, null);
                                    DataNEssenceUtil.updatePlayerData(pPlayer);
                                    pPlayer.getInventory().clearOrCountMatchingItems((item) -> item.is(getRequiredWire()), 1, pPlayer.inventoryMenu.getCraftSlots());
                                }
                            }
                        }
                    }
                } else if (pPlayer.isShiftKeyDown()) {
                    if (ent.link != null) {
                        ent.link = null;
                        ent.updateBlock();
                        ItemEntity item = new ItemEntity(pLevel, pPos.getCenter().x, pPos.getCenter().y, pPos.getCenter().z, new ItemStack(getRequiredWire()));
                        pLevel.addFreshEntity(item);
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
}
