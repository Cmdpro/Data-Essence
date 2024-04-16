package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EssenceContainer extends BlockEntity {
    private float essence;
    private float naturalEssence;
    private float exoticEssence;

    public EssenceContainer(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public float getEssence() {
        return essence;
    }
    public void setEssence(float amount) {
        essence = amount;
    }
    public float getNaturalEssence() {
        return naturalEssence;
    }
    public void setNaturalEssence(float amount) {
        naturalEssence = amount;
    }
    public float getExoticEssence() {
        return exoticEssence;
    }
    public void setExoticEssence(float amount) {
        exoticEssence = amount;
    }
}
