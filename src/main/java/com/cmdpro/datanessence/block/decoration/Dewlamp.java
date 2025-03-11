package com.cmdpro.datanessence.block.decoration;

import com.cmdpro.datanessence.client.particle.CircleParticleOptionsAdditive;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
        float hue = RandomUtils.nextFloat(0f, 1f);
        Color color = new Color( Color.HSBtoRGB(hue, 0.8f, 1f) );
        double d0 = 0.5625;

        world.addParticle(
                new CircleParticleOptionsAdditive(color), true, pos.getCenter().x, pos.getCenter().y+0.15, pos.getCenter().z, 0.0, 0.0, 0.0
        );

        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            if (!world.getBlockState(blockpos).isSolidRender(world, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getStepX() : (double)random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getStepY() : (double)random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getStepZ() : (double)random.nextFloat();
                world.addParticle(
                        new MoteParticleOptions(color, true, 120, 1f), (double)pos.getX() + d1, (double)pos.getY() + d2, (double)pos.getZ() + d3, 0.0, -0.04, 0.0
                );
            }
        }
    }
}
