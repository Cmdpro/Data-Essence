package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.api.BaseCapabilityPoint;
import com.cmdpro.datanessence.block.entity.ItemBufferBlockEntity;
import com.cmdpro.datanessence.block.entity.ItemPointBlockEntity;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ItemPoint extends BaseCapabilityPoint {

    public ItemPoint(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Item getRequiredWire() {
        return ItemRegistry.ITEM_WIRE.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ItemPointBlockEntity(pPos, pState);
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ItemBufferBlockEntity buffer) {
                buffer.drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityRegistry.ITEM_POINT.get(),
                ItemPointBlockEntity::tick);
    }
}
