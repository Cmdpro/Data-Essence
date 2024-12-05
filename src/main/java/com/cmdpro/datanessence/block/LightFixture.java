package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.api.block.RedirectorInteractable;
import com.google.errorprone.annotations.Var;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class LightFixture extends Block implements RedirectorInteractable {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty VARIANT = BooleanProperty.create("variant"); // true if alt model

    public LightFixture(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(VARIANT, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
    }
    @Override
    public boolean onRedirectorUse(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        context.getLevel().setBlockAndUpdate(context.getClickedPos(), state.setValue(VARIANT, !state.getValue(VARIANT)));
        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(VARIANT);
    }
}
