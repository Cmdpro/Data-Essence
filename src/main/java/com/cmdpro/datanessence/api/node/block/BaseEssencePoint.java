package com.cmdpro.datanessence.api.node.block;

import com.cmdpro.datanessence.api.node.item.INodeUpgrade;
import com.cmdpro.datanessence.api.node.EssenceNodePath;
import com.cmdpro.datanessence.api.util.PlayerDataUtil;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
            if (direction.getAxis() == Direction.Axis.Y) {
                return this.defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, pContext.getHorizontalDirection());
            } else {
                return this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction.getOpposite());
            }
        }

        return null;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.getBlockEntity(pPos) instanceof BaseEssencePointBlockEntity ent) {
            for (BlockPos i : ent.link) {
                ItemEntity item = new ItemEntity(pLevel, pPos.getCenter().x, pPos.getCenter().y, pPos.getCenter().z, new ItemStack(getRequiredWire()));
                pLevel.addFreshEntity(item);
            }
            for (BlockPos i : ent.linkFrom) {
                if (pLevel.getBlockEntity(i) instanceof BaseEssencePointBlockEntity ent2) {
                    ent2.link.remove(pPos);
                    EssenceNodePath.updatePaths(ent2);
                    ent2.updateBlock();
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
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

    public abstract Item getRequiredWire();
    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        boolean success = pPlayer.getItemInHand(pHand).is(getRequiredWire()) || pPlayer.getItemInHand(pHand).getItem() instanceof INodeUpgrade;
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof BaseEssencePointBlockEntity ent) {
                if (pPlayer.getItemInHand(pHand).is(getRequiredWire())) {
                    Optional<BlockEntity> linkFrom = pPlayer.getData(AttachmentTypeRegistry.LINK_FROM);
                    if (!linkFrom.isPresent()) {
                        if (ent.link.size() <= DataNEssenceConfig.maxNodeWires) {
                            pPlayer.setData(AttachmentTypeRegistry.LINK_FROM, Optional.of(ent));
                            PlayerDataUtil.updateData((ServerPlayer) pPlayer);
                            pLevel.playSound(null, pPos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1f, 1.1f);
                        }
                    } else {
                        if (linkFrom.get().getBlockState().getBlock() instanceof BaseEssencePoint other) {
                            if (other.getRequiredWire() == getRequiredWire() && ent != linkFrom.get() && (ent.link.isEmpty() || !ent.link.contains(linkFrom.get().getBlockPos()))) {
                                if (linkFrom.get() instanceof BaseEssencePointBlockEntity linkFrom2) {
                                    if (!linkFrom2.link.contains(pPos)) {
                                        linkFrom2.link.add(pPos);
                                        EssenceNodePath.updatePaths(linkFrom2);
                                        if (pLevel.getBlockEntity(pPos) instanceof BaseEssencePointBlockEntity linkTo) {
                                            linkTo.linkFrom.add(linkFrom2.getBlockPos());
                                        }
                                    }
                                    linkFrom2.updateBlock();
                                    pPlayer.setData(AttachmentTypeRegistry.LINK_FROM, Optional.empty());
                                    PlayerDataUtil.updateData((ServerPlayer) pPlayer);
                                    pPlayer.getInventory().clearOrCountMatchingItems((item) -> item.is(getRequiredWire()), 1, pPlayer.inventoryMenu.getCraftSlots());
                                    pLevel.playSound(null, pPos, SoundEvents.CROSSBOW_LOADING_END.value(), SoundSource.BLOCKS, 1f, 1.1f);
                                }
                            }
                        }
                    }
                }
                if (pPlayer.getItemInHand(pHand).getItem() instanceof INodeUpgrade upgrade) {
                    if (ent.universalUpgrade.getStackInSlot(0).isEmpty() && upgrade.getType().equals(INodeUpgrade.Type.UNIVERSAL)) {
                        ItemStack copy = pPlayer.getItemInHand(pHand).copy();
                        copy.setCount(1);
                        ent.universalUpgrade.setStackInSlot(0, copy);
                        pPlayer.getItemInHand(pHand).shrink(1);
                        ent.updateBlock();
                    } else if (ent.uniqueUpgrade.getStackInSlot(0).isEmpty() && upgrade.getType().equals(INodeUpgrade.Type.UNIQUE)) {
                        ItemStack copy = pPlayer.getItemInHand(pHand).copy();
                        copy.setCount(1);
                        ent.uniqueUpgrade.setStackInSlot(0, copy);
                        pPlayer.getItemInHand(pHand).shrink(1);
                        ent.updateBlock();
                    }
                }
                if (pPlayer.getItemInHand(pHand).is(ItemRegistry.ESSENCE_REDIRECTOR.get())) {
                    if (!ent.universalUpgrade.getStackInSlot(0).isEmpty()) {
                        pPlayer.getInventory().add(ent.universalUpgrade.getStackInSlot(0));
                        ent.universalUpgrade.setStackInSlot(0, ItemStack.EMPTY);
                        ent.updateBlock();
                    } else if (!ent.uniqueUpgrade.getStackInSlot(0).isEmpty()) {
                        pPlayer.getInventory().add(ent.uniqueUpgrade.getStackInSlot(0));
                        ent.uniqueUpgrade.setStackInSlot(0, ItemStack.EMPTY);
                        ent.updateBlock();
                    }
                }
            }
        }
        if (success) {
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof BaseEssencePointBlockEntity ent) {
                if (pPlayer.isShiftKeyDown()) {
                    if (!ent.link.isEmpty()) {
                        for (BlockPos i : ent.link) {
                            if (pLevel.getBlockEntity(i) instanceof BaseEssencePointBlockEntity linkTo) {
                                linkTo.linkFrom.remove(ent.getBlockPos());
                            }
                        }
                        for (BlockPos i : ent.link) {
                            ItemEntity item = new ItemEntity(pLevel, pPos.getCenter().x, pPos.getCenter().y, pPos.getCenter().z, new ItemStack(getRequiredWire()));
                            pLevel.addFreshEntity(item);
                        }
                        ent.link.clear();
                        EssenceNodePath.updatePaths(ent);
                        ent.updateBlock();
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return getConnectedDirection(pState).getOpposite() == pFacing && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
}