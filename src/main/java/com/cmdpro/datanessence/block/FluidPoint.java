package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.api.BaseCapabilityPoint;
import com.cmdpro.datanessence.block.entity.FluidPointBlockEntity;
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

public class FluidPoint extends BaseCapabilityPoint {

    public FluidPoint(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Item getRequiredWire() {
        return ItemRegistry.FLUID_WIRE.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FluidPointBlockEntity(pPos, pState);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityRegistry.FLUID_POINT.get(),
                FluidPointBlockEntity::tick);
    }
}
