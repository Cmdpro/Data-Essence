package com.cmdpro.datanessence.block.world;

import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.RandomUtils;

import java.awt.*;

public class CrystallineLeaves extends TransparentBlock {

    public CrystallineLeaves(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        float hue = RandomUtils.nextFloat(0f, 1f);
        Color color = new Color( Color.HSBtoRGB(hue, 0.8f, 1f) );
        double d0 = 0.5625;

        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            if (!world.getBlockState(blockpos).isSolidRender(world, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getStepX() : (double)random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getStepY() : (double)random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getStepZ() : (double)random.nextFloat();
                world.addParticle(
                        new MoteParticleOptions(color, true, 40), (double)pos.getX() + d1, (double)pos.getY() + d2, (double)pos.getZ() + d3, 0.0, -0.01, 0.0
                );
            }
        }
    }
}
