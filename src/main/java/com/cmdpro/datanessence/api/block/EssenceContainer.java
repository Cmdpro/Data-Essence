package com.cmdpro.datanessence.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EssenceContainer extends BlockEntity {
    private float essence;
    private float naturalEssence;
    private float exoticEssence;
    private float lunarEssence;

    public EssenceContainer(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public float getEssence() {
        return essence;
    }
    public void setEssence(float amount) {
        essence = amount;
    }
    public float getMaxEssence() {
        return 0;
    }
    public float getLunarEssence() {
        return lunarEssence;
    }
    public void setLunarEssence(float amount) {
        lunarEssence = amount;
    }
    public float getMaxLunarEssence() {
        return 0;
    }
    public float getNaturalEssence() {
        return naturalEssence;
    }
    public void setNaturalEssence(float amount) {
        naturalEssence = amount;
    }
    public float getMaxNaturalEssence() {
        return 0;
    }
    public float getExoticEssence() {
        return exoticEssence;
    }
    public void setExoticEssence(float amount) {
        exoticEssence = amount;
    }
    public float getMaxExoticEssence() {
        return 0;
    }
}
