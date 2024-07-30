package com.cmdpro.datanessence.block.storage;

import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putFloat("lunarEssence", getLunarEssence());
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        setLunarEssence(nbt.getFloat("lunarEssence"));
    }
}
