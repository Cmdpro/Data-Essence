package com.cmdpro.datanessence.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.Nullable;

public class EssenceCrystal extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final IntegerProperty MODEL = IntegerProperty.create("model", 0, 2);
    protected final VoxelShape northAabb;
    protected final VoxelShape southAabb;
    protected final VoxelShape eastAabb;
    protected final VoxelShape westAabb;
    protected final VoxelShape upAabb;
    protected final VoxelShape downAabb;


    public EssenceCrystal(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP).setValue(MODEL, 0));
        int pOffset = 4;
        int pSize = 8;
        this.upAabb = Block.box((double)pOffset, 0.0D, (double)pOffset, (double)(16 - pOffset), (double)pSize, (double)(16 - pOffset));
        this.downAabb = Block.box((double)pOffset, (double)(16 - pSize), (double)pOffset, (double)(16 - pOffset), 16.0D, (double)(16 - pOffset));
        this.northAabb = Block.box((double)pOffset, (double)pOffset, (double)(16 - pSize), (double)(16 - pOffset), (double)(16 - pOffset), 16.0D);
        this.southAabb = Block.box((double)pOffset, (double)pOffset, 0.0D, (double)(16 - pOffset), (double)(16 - pOffset), (double)pSize);
        this.eastAabb = Block.box(0.0D, (double)pOffset, (double)pOffset, (double)pSize, (double)(16 - pOffset), (double)(16 - pOffset));
        this.westAabb = Block.box((double)(16 - pSize), (double)pOffset, (double)pOffset, 16.0D, (double)(16 - pOffset), (double)(16 - pOffset));
    }
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);
        switch (direction) {
            case NORTH:
                return this.northAabb;
            case SOUTH:
                return this.southAabb;
            case EAST:
                return this.eastAabb;
            case WEST:
                return this.westAabb;
            case DOWN:
                return this.downAabb;
            case UP:
            default:
                return this.upAabb;
        }
    }
    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        Vec3 offset = new Vec3(RandomUtils.nextFloat(0, 0.8f)-0.4f, RandomUtils.nextFloat(0, 0.8f)-0.4f, RandomUtils.nextFloat(0, 0.8f)-0.4f);
        pLevel.addParticle(ParticleRegistry.ESSENCE_SPARKLE.get(), pPos.getCenter().x+offset.x, pPos.getCenter().y+offset.y, pPos.getCenter().z+offset.z, 0.0D, 0.0D, 0.0D);
    }
    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        Direction direction = pState.getValue(FACING);
        BlockPos blockpos = pPos.relative(direction.getOpposite());
        return pLevel.getBlockState(blockpos).isFaceSturdy(pLevel, blockpos, direction);
    }
    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return pDirection == pState.getValue(FACING).getOpposite() && !pState.canSurvive(pLevel, pPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getClickedFace()).setValue(MODEL, pContext.getPlayer().getRandom().nextInt(0, 3));
    }
    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }
    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, MODEL);
    }
}
