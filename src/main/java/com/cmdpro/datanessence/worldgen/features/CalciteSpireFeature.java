package com.cmdpro.datanessence.worldgen.features;

import com.cmdpro.datanessence.block.world.EssenceCrystal;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.joml.Vector2i;
import org.joml.Vector3i;

public class CalciteSpireFeature extends Feature<NoneFeatureConfiguration> {
    public CalciteSpireFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        Vec3i offset = new Vec3i(8+pContext.random().nextIntBetweenInclusive(-4, 4), 0, 8+pContext.random().nextIntBetweenInclusive(-8, 8));
        while (pContext.level().getBlockState(pContext.origin().offset(offset)).isAir()) {
            offset = offset.offset(0, -1, 0);
        }
        float dirChange = pContext.random().nextBoolean() ? -(5+(pContext.random().nextFloat()*10)) : 5+(pContext.random().nextFloat()*10);
        BlockPos origin = pContext.origin().offset(offset);
        float dir = pContext.random().nextFloat()*360;
        for (int y = -5; y <= 50; y++) {
            float range = (50-y)/10f;
            if (y < 0) {
                range = 5-(y*-1);
            }
            float size = 2;
            if (y < 0) {
                size *= 1f-((y*-1f)/5f);
            }
            if (y > 45) {
                size *= 1f-((y-45)/5f);
            }
            for (int i = 0; i < 2; i++) {
                float dirOffset = i*180;
                BlockPos posCenter = origin.offset(new Vec3i((int) (Math.cos(Math.toRadians(dir+dirOffset)) * ((range * 2f) + 4)), y, (int) (Math.sin(Math.toRadians(dir+dirOffset)) * ((range * 2f) + 4))));
                for (int x = (int)-Math.floor(size); x <= Math.ceil(size); x++) {
                    for (int z = (int)-Math.floor(size); z <= Math.ceil(size); z++) {
                        for (int y2 = (int)-Math.floor(size); y2 <= Math.ceil(size); y2++) {
                            BlockPos pos = posCenter.offset(new Vec3i(x, y2, z));
                            if (posCenter.getCenter().distanceTo(pos.getCenter()) <= size) {
                                if (pContext.level().getBlockState(pos).canBeReplaced()) {
                                    setBlock(pContext.level(), pos, BlockRegistry.SPIRE_GLASS.get().defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
            dir += dirChange;
            for (int x = -10; x <= 10; x++) {
                for (int z = -10; z <= 10; z++) {
                    BlockPos pos = origin.offset(new Vec3i(x, y, z));
                    if (origin.offset(0, y, 0).getCenter().distanceTo(pos.getCenter()) <= range) {
                        setBlock(pContext.level(), pos, Blocks.CALCITE.defaultBlockState());
                    }
                }
            }
        }
        return true;
    }
}
