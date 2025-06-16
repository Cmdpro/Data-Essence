package com.cmdpro.datanessence.block.technical.cryochamber;

import com.cmdpro.databank.megablock.BasicMegablockCore;
import com.cmdpro.databank.megablock.MegablockCoreUtil;
import com.cmdpro.databank.megablock.MegablockShape;
import com.cmdpro.datanessence.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class Cryochamber extends BasicMegablockCore implements EntityBlock {
    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    public Cryochamber(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        BlockState above = level.getBlockState(pos.above());
        if (above.is(getRouterBlock())) {
            above = above.setValue(CryochamberRouter.INVISIBLE, false);
            level.setBlockAndUpdate(pos.above(), above);
        }
    }
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (state.getBlock() != newState.getBlock()) {
            level.setBlockAndUpdate(pos, BlockRegistry.AREKKO.get().defaultBlockState());
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CryochamberBlockEntity(pos, state);
    }

    @Override
    public MegablockShape getMegablockShape() {
        return new MegablockShape(Vec3i.ZERO, new Vec3i(0, 2, 0));
    }

    @Override
    public Block getRouterBlock() {
        return BlockRegistry.CRYOCHAMBER_FILLER.get();
    }
}
