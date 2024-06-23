package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ExoticEssenceBatteryBlockEntity extends EssenceContainer {
    public ExoticEssenceBatteryBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.EXOTIC_ESSENCE_BATTERY.get(), pos, state);
    }
    public ExoticEssenceBatteryBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    public float getMaxExoticEssence() {
        return DataNEssenceConfig.essenceBatteryMax;
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("exoticEssence", getExoticEssence());
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        setExoticEssence(nbt.getFloat("exoticEssence"));
    }
}
