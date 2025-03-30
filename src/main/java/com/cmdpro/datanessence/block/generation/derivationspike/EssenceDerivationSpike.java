package com.cmdpro.datanessence.block.generation.derivationspike;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EssenceDerivationSpike extends Block implements EntityBlock {
    private static final VoxelShape SHAPE_BASE = Block.box(0, 0, 0, 16, 10, 16);
    private static final VoxelShape SHAPE_SPIKE = Block.box(7, 10, 7, 9, 16, 9);

    private static final VoxelShape SHAPE = Shapes.join(SHAPE_BASE, SHAPE_SPIKE, BooleanOp.ONLY_FIRST);

    public EssenceDerivationSpike(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EssenceDerivationSpikeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof EssenceDerivationSpikeBlockEntity ent) {
                EssenceDerivationSpikeBlockEntity.tick(lvl, pos, st, ent);
            }
        };
    }
}
