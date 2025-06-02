package com.cmdpro.datanessence.block.decoration;

import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.RandomUtils;

import java.awt.*;

public class Dewlamp extends Block {
    private static final VoxelShape SHAPE = Block.box(4.0D, 6.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public Dewlamp(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.relative(Direction.UP), Direction.UP);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        float hue = Mth.nextFloat(random, 0f, 1f);
        int lifetime = random.nextIntBetweenInclusive(60, 180);
        Color color = new Color( Color.HSBtoRGB(hue, 0.8f, 1f) );
        double d0 = 0.2; // 0.5625;
        float horizontalVelocity = random.nextFloat() * 0.02f;
        horizontalVelocity = random.nextInt() % 2 == 0 ? horizontalVelocity : -horizontalVelocity;

        // center light
        world.addParticle(
                new CircleParticleOptions().setColor(color).setAdditive(true), true, pos.getCenter().x, pos.getCenter().y+0.15, pos.getCenter().z, 0.0, 0.0, 0.0
        );

        // light dew
        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            if (!world.getBlockState(blockpos).isSolidRender(world, blockpos) && direction != Direction.UP && direction != Direction.DOWN) {
                Direction.Axis axis = direction.getAxis();
                double d1 = axis == Direction.Axis.X ? 0.5 + d0 * (double)direction.getStepX() : (double)random.nextFloat();
                double d2 = axis == Direction.Axis.Y ? 0.5 + d0 * (double)direction.getStepY() : (double)random.nextFloat();
                double d3 = axis == Direction.Axis.Z ? 0.5 + d0 * (double)direction.getStepZ() : (double)random.nextFloat();
                world.addParticle(
                        new MoteParticleOptions().setColor(color).setAdditive(true).setLifetime(lifetime).setFriction(1f).setGravity(0.04f), (double)pos.getX() + d1, (double)pos.getY() + d2, (double)pos.getZ() + d3, horizontalVelocity, 0, horizontalVelocity
                );
            }
        }
    }
}
