package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.api.BaseEssencePoint;
import com.cmdpro.datanessence.block.entity.ExoticEssencePointBlockEntity;
import com.cmdpro.datanessence.init.BlockEntityInit;
import com.cmdpro.datanessence.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ExoticEssencePoint extends BaseEssencePoint {

    public ExoticEssencePoint(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Item getRequiredWire() {
        return ItemInit.EXOTICESSENCEWIRE.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ExoticEssencePointBlockEntity(pPos, pState);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityInit.EXOTICESSENCEPOINT.get(),
                ExoticEssencePointBlockEntity::tick);
    }
}
