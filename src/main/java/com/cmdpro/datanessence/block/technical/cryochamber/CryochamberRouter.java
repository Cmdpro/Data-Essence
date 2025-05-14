package com.cmdpro.datanessence.block.technical.cryochamber;

import com.cmdpro.databank.megablock.MegablockRouter;
import com.cmdpro.datanessence.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CryochamberRouter extends MegablockRouter {
    private static final VoxelShape MIDDLE_SHAPE =  Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape TOP_SHAPE =  Block.box(0, 0, 0, 16, 5, 16);
    public static final Property<Boolean> INVISIBLE = BooleanProperty.create("invisible");
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pLevel instanceof Level level) {
            if (new Vec3i(0, 2, 0).equals(findOffset(level, pPos))) {
                return TOP_SHAPE;
            }
        }
        return MIDDLE_SHAPE;
    }
    public CryochamberRouter(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(INVISIBLE, true));
    }

    @Override
    public Block getCore() {
        return BlockRegistry.CRYOCHAMBER.get();
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        if (pState.getValue(INVISIBLE)) {
            return RenderShape.INVISIBLE;
        }
        return RenderShape.MODEL;
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(INVISIBLE);
    }
}
