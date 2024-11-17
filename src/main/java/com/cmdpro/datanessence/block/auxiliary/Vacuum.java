package com.cmdpro.datanessence.block.auxiliary;

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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class Vacuum extends Block implements EntityBlock {
    private static final VoxelShape SHAPE =  Shapes.or(
            //Top
            Block.box(0, 14, 0, 14, 16, 2),
            Block.box(0, 14, 2, 2, 16, 16),
            Block.box(2, 14, 14, 16, 16, 16),
            Block.box(14, 14, 0, 16, 16, 14),

            //Middle
            Block.box(14, 2, 14, 16, 14, 16),
            Block.box(14, 2, 0, 16, 14, 2),
            Block.box(0, 2, 0, 2, 14, 2),
            Block.box(0, 2, 14, 2, 14, 16),

            //Bottom
            Block.box(0, 0, 0, 14, 2, 2),
            Block.box(0, 0, 2, 2, 2, 16),
            Block.box(2, 0, 14, 16, 2, 16),
            Block.box(14, 0, 0, 16, 2, 14)
    );

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public Vacuum(Properties pProperties) {
        super(pProperties);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new VacuumBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof VacuumBlockEntity ent) {
                VacuumBlockEntity.tick(lvl, pos, st, ent);
            }
        };
    }
}
