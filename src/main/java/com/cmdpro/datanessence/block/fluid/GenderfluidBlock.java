package com.cmdpro.datanessence.block.fluid;

import com.cmdpro.datanessence.registry.FluidRegistry;
import com.cmdpro.datanessence.registry.MobEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class GenderfluidBlock extends LiquidBlock {
    public GenderfluidBlock(Properties properties) {
        super(FluidRegistry.GENDERFLUID.get(), properties);
    }

    @Override
    protected void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        super.entityInside(pState, pLevel, pPos, pEntity);
        if (pEntity instanceof LivingEntity ent) {
            ent.addEffect(new MobEffectInstance(MobEffectRegistry.GENDER_EUPHORIA, 20*5));
        }
    }
}
