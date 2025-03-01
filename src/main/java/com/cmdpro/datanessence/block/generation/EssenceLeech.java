package com.cmdpro.datanessence.block.generation;

import com.cmdpro.datanessence.particle.CircleParticleOptionsAdditive;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class EssenceLeech extends Block implements EntityBlock {
    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public EssenceLeech(Properties pProperties) {
        super(pProperties);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EssenceLeechBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof EssenceLeechBlockEntity ent) {
                EssenceLeechBlockEntity.tick(lvl, pos, st, ent);
            }
        };
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        Vec3 offset = new Vec3(RandomUtils.nextFloat(0, 0.8f)-0.4f, RandomUtils.nextFloat(0, 0.8f)-0.4f, RandomUtils.nextFloat(0, 0.8f)-0.4f);
        world.addParticle(new CircleParticleOptionsAdditive( new Color(255, random.nextInt(255), 255) ), pos.getCenter().x+offset.x, pos.getCenter().y+0.6, pos.getCenter().z+offset.z, 0.0D, 0.07D, 0.0D);
    }
}
