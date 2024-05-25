package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.api.BaseEssencePoint;
import com.cmdpro.datanessence.block.entity.NaturalEssencePointBlockEntity;
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

public class NaturalEssencePoint extends BaseEssencePoint {

    public NaturalEssencePoint(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Item getRequiredWire() {
        return ItemInit.NATURALESSENCEWIRE.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NaturalEssencePointBlockEntity(pPos, pState);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityInit.NATURALESSENCEPOINT.get(),
                NaturalEssencePointBlockEntity::tick);
    }
}
