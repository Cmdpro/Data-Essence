package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.block.entity.StructureProtectorBlockEntity;
import com.cmdpro.datanessence.block.entity.VacuumBlockEntity;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StructureProtector extends Block implements EntityBlock {
    public StructureProtector(Properties pProperties) {
        super(pProperties);
    }
    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 16, 16);
    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof StructureProtectorBlockEntity ent) {
            if (pPlayer.isCreative()) {
                if (!pLevel.isClientSide()) {
                    ent.bindProcess = 1;
                    pPlayer.sendSystemMessage(Component.translatable("block.datanessence.structure_controller.select_pos_1"));
                    pPlayer.setData(AttachmentTypeRegistry.BINDING_STRUCTURE_CONTROLLER, Optional.of(ent));
                    return InteractionResult.sidedSuccess(pLevel.isClientSide());
                }
            }
        }
        if (pLevel.isClientSide) {
            return InteractionResult.sidedSuccess(pLevel.isClientSide());
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new StructureProtectorBlockEntity(pPos, pState);
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.getBlockEntity(pPos) instanceof StructureProtectorBlockEntity ent) {
            pLevel.getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS).remove(ent);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof StructureProtectorBlockEntity ent) {
                StructureProtectorBlockEntity.tick(lvl, pos, st, ent);
            }
        };
    }
}
