package com.cmdpro.datanessence.block.world;

import com.cmdpro.datanessence.registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SanctuaryGrass extends TallGrassBlock {
    public SanctuaryGrass(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextIntBetweenInclusive(0, 5) == 0) {
            Vec3 offset = new Vec3(Mth.nextFloat(random, -0.4f, 0.4f), Mth.nextFloat(random, -0.4f, 1.6f), Mth.nextFloat(random, -0.4f, 0.4f));
            float speed = Mth.nextFloat(random, 0.025f, 0.075f);
            level.addParticle(ParticleRegistry.SANCTUARY_SPARKLE.get(), pos.getCenter().x + offset.x, pos.getCenter().y + offset.y, pos.getCenter().z + offset.z, 0.0D, speed, 0.0D);
        }
    }
}
