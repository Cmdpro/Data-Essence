package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class LunarEssenceBatteryBlockEntity extends EssenceContainer {
    public LunarEssenceBatteryBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LUNAR_ESSENCE_BATTERY.get(), pos, state);
    }

    @Override
    public float getMaxLunarEssence() {
        return DataNEssenceConfig.essenceBatteryMax;
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("lunarEssence", getLunarEssence());
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        setLunarEssence(nbt.getFloat("lunarEssence"));
    }
}
