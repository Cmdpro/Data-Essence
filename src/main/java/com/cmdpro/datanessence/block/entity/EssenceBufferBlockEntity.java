package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EssenceBufferBlockEntity extends EssenceContainer {
    public EssenceBufferBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.ESSENCEBUFFER.get(), pPos, pBlockState);
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
