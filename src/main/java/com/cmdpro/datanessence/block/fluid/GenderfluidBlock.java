package com.cmdpro.datanessence.block.fluid;

import com.cmdpro.datanessence.api.block.SpreadingPlant;
import com.cmdpro.datanessence.registry.FluidRegistry;
import com.cmdpro.datanessence.registry.MobEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.AABB;

import java.util.stream.Stream;

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

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        // in a volume of 9x3x9 centered around the fluid (as if you were hydrating farmland with it),
        // randomly grow SpreadingPlants.
        for (BlockPos queryPos : BlockPos.betweenClosed(pos.offset(-4, -1, -4), pos.offset(4, 1, 4))) {
            BlockState queryState = world.getBlockState(queryPos);

            if (queryState.getBlock() instanceof SpreadingPlant plant) {
                plant.grow(queryState, world, queryPos, world.getRandom(), 7); // adjust maybe?
            }
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState pState) {
        return true;
    }
}
