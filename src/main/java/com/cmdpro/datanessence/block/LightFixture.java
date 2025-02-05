package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.api.block.RedirectorInteractable;
import com.cmdpro.datanessence.particle.MoteParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.RandomUtils;

import java.awt.*;

public class LightFixture extends Block implements RedirectorInteractable {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty VARIANT = BooleanProperty.create("variant"); // true if alt model

    public LightFixture(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(VARIANT, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        Vec3 offset = new Vec3(RandomUtils.nextFloat(0, 0.8f)-0.4f, RandomUtils.nextFloat(0, 0.8f)-0.4f, RandomUtils.nextFloat(0, 0.8f)-0.4f);
        pLevel.addParticle(new MoteParticleOptions(new Color(0xFFD360), true), pPos.getCenter().x+offset.x, pPos.getCenter().y+offset.y, pPos.getCenter().z+offset.z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean onRedirectorUse(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        context.getLevel().setBlockAndUpdate(context.getClickedPos(), state.setValue(VARIANT, !state.getValue(VARIANT)));
        return true;
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING).add(VARIANT);
    }
}
