package com.cmdpro.datanessence.api.block;

import com.cmdpro.datanessence.api.util.DataBankUtil;
import com.cmdpro.datanessence.data.databank.DataBankTypeManager;
import com.cmdpro.datanessence.item.DataTablet;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class BaseDataBankBlock extends Block {
    public BaseDataBankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide) {
            if (isOkayToOpen(pState, pLevel, pPos, pPlayer, pHitResult)) {
                if (canUse(pState, pLevel, pPos, pPlayer, pHitResult)) {
                    DataBankUtil.sendDataBankEntries(pPlayer, getEntries(pState, pLevel, pPos, pPlayer, pHitResult));
                }
                else {
                    pPlayer.displayClientMessage(Component.translatable("block.datanessence.data_bank.cannot_use"), true);
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
    public boolean canUse(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        return pPlayer.getInventory().contains((stack) -> stack.getItem() instanceof DataTablet);
    }
    public boolean isOkayToOpen(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        return true;
    }
    public abstract ResourceLocation[] getEntries(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult);
}
