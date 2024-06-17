package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EssenceBufferBlockEntity extends EssenceContainer {
    public EssenceBufferBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.ESSENCE_BUFFER.get(), pPos, pBlockState);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        super.saveAdditional(tag);
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        setEssence(nbt.getFloat("essence"));
        setLunarEssence(nbt.getFloat("lunarEssence"));
        setNaturalEssence(nbt.getFloat("naturalEssence"));
        setExoticEssence(nbt.getFloat("exoticEssence"));
    }

    @Override
    public float getMaxEssence() {
        return 100;
    }
    @Override
    public float getMaxLunarEssence() {
        return 100;
    }
    @Override
    public float getMaxNaturalEssence() {
        return 100;
    }
    @Override
    public float getMaxExoticEssence() {
        return 100;
    }
}
