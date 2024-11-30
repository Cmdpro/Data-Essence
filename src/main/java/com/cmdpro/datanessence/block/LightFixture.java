package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.api.block.RedirectorInteractable;
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
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, false));
    }

    @Override
    public boolean onRedirectorUse(UseOnContext context) {

        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(VARIANT);
    }
}
