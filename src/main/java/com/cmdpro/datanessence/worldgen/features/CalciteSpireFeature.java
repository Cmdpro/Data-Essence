package com.cmdpro.datanessence.worldgen.features;

import com.cmdpro.datanessence.block.world.EssenceCrystal;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
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
        float height = 50*(Mth.nextFloat(pContext.random(), 0.5f, 1.5f));
        float spiralSize = Mth.nextFloat(pContext.random(), 0.75f, 1f);
        float dirChange = pContext.random().nextBoolean() ? Mth.nextFloat(pContext.random(), -15f, -5f) : Mth.nextFloat(pContext.random(), 5f, 10f);
        BlockPos origin = pContext.origin();
        if (!pContext.level().getBlockState(origin.below()).getFluidState().isEmpty()) {
            return false;
        }
        float dir = Mth.nextFloat(pContext.random(), 0f, 360f);
        for (int y = -5; y <= height; y++) {
            float range = ((height-y)/10f);
            if (y < 0) {
                range = 5-(y*-1);
            }
            float size = ((height-y)/15f);
            if (y < 0) {
                size *= 1f-((y*-1f)/5f);
            }
            size *= spiralSize;
            float yDivideForSpeed = 20f;
            int splits = (int)(3 * Math.clamp(y / yDivideForSpeed, 1, Float.MAX_VALUE));
            for (int s = 0; s < splits; s++) {
                for (int i = 0; i < 2; i++) {
                    float dirOffset = i * 180;
                    BlockPos posCenter = origin.offset(new Vec3i((int) (Math.cos(Math.toRadians(dir + dirOffset)) * ((range * 2f) + 4)), y, (int) (Math.sin(Math.toRadians(dir + dirOffset)) * ((range * 2f) + 4))));
                    for (int x = (int) -Math.floor(size); x <= Math.ceil(size); x++) {
                        for (int z = (int) -Math.floor(size); z <= Math.ceil(size); z++) {
                            for (int y2 = (int) -Math.floor(size); y2 <= Math.ceil(size); y2++) {
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
                dir += (dirChange * Math.clamp(y / yDivideForSpeed, 1, Float.MAX_VALUE)) / splits;
            }
            for (int x = -10; x <= 10; x++) {
                for (int z = -10; z <= 10; z++) {
                    BlockPos pos = origin.offset(new Vec3i(x, y, z));
                    double dist = origin.offset(0, y, 0).getCenter().distanceTo(pos.getCenter());
                    if (dist <= range) {
                        if (dist <= 1 || (dist+1 <= range || pContext.random().nextIntBetweenInclusive(0, 4) != 0)) {
                            setBlock(pContext.level(), pos, Blocks.CALCITE.defaultBlockState());
                        }
                    }
                }
            }
        }
        return true;
    }
}
