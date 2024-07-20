package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.block.entity.ComputerBlockEntity;
import com.cmdpro.datanessence.block.entity.StructureControllerBlockEntity;
import com.cmdpro.datanessence.computers.ComputerTypeManager;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StructureController extends Block implements EntityBlock {
    public StructureController(Properties pProperties) {
        super(pProperties);
    }
    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 16, 16);
    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof StructureControllerBlockEntity ent) {
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
        return new StructureControllerBlockEntity(pPos, pState);
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.getBlockEntity(pPos) instanceof StructureControllerBlockEntity ent) {
            pLevel.getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS).remove(ent);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }
}
