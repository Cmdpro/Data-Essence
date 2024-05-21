package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.block.entity.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.init.BlockEntityInit;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class BaseEssencePoint extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    protected static final VoxelShape WEST_AABB = Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
    protected static final VoxelShape UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
    protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);

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

    public abstract Item getRequiredWire();
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof BaseEssencePointBlockEntity ent) {
                if (pPlayer.isHolding(getRequiredWire())) {
                    pPlayer.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent((data) -> {
                        if (data.getLinkFrom() == null) {
                            if (ent.link == null) {
                                data.setLinkFrom(ent);
                            }
                        } else {
                            if (data.getLinkFrom().getBlockState().getBlock() instanceof BaseEssencePoint other) {
                                if (other.getRequiredWire() == getRequiredWire() && ent != data.getLinkFrom()) {
                                    data.getLinkFrom().link = pPos;
                                    data.getLinkFrom().updateBlock();
                                    data.setLinkFrom(null);
                                    pPlayer.getInventory().clearOrCountMatchingItems((item) -> item.is(getRequiredWire()), 1, pPlayer.inventoryMenu.getCraftSlots());
                                }
                            }
                        }
                    });
                } else if (pPlayer.isShiftKeyDown()) {
                    if (ent.link != null) {
                        ent.link = null;
                        ent.updateBlock();
                        pPlayer.addItem(new ItemStack(getRequiredWire()));
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
}
