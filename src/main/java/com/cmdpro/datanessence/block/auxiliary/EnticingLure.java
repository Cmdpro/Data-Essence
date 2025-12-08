package com.cmdpro.datanessence.block.auxiliary;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class EnticingLure extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 12, 11);

    public EnticingLure(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EnticingLureBlockEntity(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof EnticingLureBlockEntity ent) {
                ent.drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof EnticingLureBlockEntity ent) {
                EnticingLureBlockEntity.tick(lvl, pos, st, ent);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof EnticingLureBlockEntity ent)) {
            return InteractionResult.PASS;
        }

        IItemHandler handler = ent.getItemHandler();
        ItemStack inSlot = handler.getStackInSlot(0);

        if (inSlot.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!pLevel.isClientSide()) {
            ItemStack extracted = handler.extractItem(0, inSlot.getCount(), false);

            if (pPlayer.getMainHandItem().isEmpty()) {
                pPlayer.setItemInHand(InteractionHand.MAIN_HAND, extracted);
            } else if (!pPlayer.addItem(extracted)) {
                pPlayer.drop(extracted, false);
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof EnticingLureBlockEntity ent)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        IItemHandler handler = ent.getItemHandler();
        ItemStack inSlot = handler.getStackInSlot(0);

        if (!inSlot.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide()) {
            ItemStack toInsert = held.split(1);
            ItemStack leftover = handler.insertItem(0, toInsert, false);

            if (!leftover.isEmpty()) {
                if (!player.addItem(leftover)) {
                    player.drop(leftover, false);
                }
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }
}
