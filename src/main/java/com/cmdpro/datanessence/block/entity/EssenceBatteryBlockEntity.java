package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EssenceBatteryBlockEntity extends EssenceContainer {
    public EssenceBatteryBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.ESSENCE_BATTERY.get(), pos, state);
    }

    @Override
    public float getMaxEssence() {
        return DataNEssenceConfig.essenceBatteryMax;
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("essence", getEssence());
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        setEssence(nbt.getFloat("essence"));
    }
}
