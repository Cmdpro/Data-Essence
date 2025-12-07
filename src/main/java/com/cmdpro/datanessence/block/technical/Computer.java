package com.cmdpro.datanessence.block.technical;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.ComputerUtil;
import com.cmdpro.datanessence.data.computers.ComputerTypeManager;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class Computer extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public Computer(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    private static final VoxelShape SHAPE_FALLOFF =  Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_WEST =  Shapes.or(
            Block.box(1, 0, 1, 15, 4, 15),
            Block.box(2, 4, 2.5f, 13, 14, 13.5f)
    );
    private static final VoxelShape SHAPE_EAST =  Shapes.or(
            Block.box(1, 0, 1, 15, 4, 15),
            Block.box(3, 4, 2.5f, 14, 14, 13.5f)
    );
    private static final VoxelShape SHAPE_SOUTH =  Shapes.or(
            Block.box(1, 0, 1, 15, 4, 15),
            Block.box(2.5, 4, 3, 13.5, 14, 14)
    );
    private static final VoxelShape SHAPE_NORTH =  Shapes.or(
            Block.box(1, 0, 1, 15, 4, 15),
            Block.box(2.5, 4, 2, 13.5, 14, 13)
    );

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide) {
            giveLocator(pLevel, pPos, pPlayer);

            if (pLevel.getBlockEntity(pPos) instanceof ComputerBlockEntity ent) {
                if (ent.type != null && ComputerTypeManager.types.containsKey(ent.type)) {
                    ComputerUtil.openComputer(pPlayer, ComputerTypeManager.types.get(ent.type));
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    public void giveLocator(Level world, BlockPos pos, Player player) {
        ItemStack signalTracker = new ItemStack(ItemRegistry.LOCATOR.get(), 1);
        ServerLevel serverWorld = ((ServerLevel) world);
        boolean hasGottenDataTablet = false;

        if ( player instanceof ServerPlayer serverPlayer ) {
            AdvancementHolder advancement = serverWorld.getServer().getAdvancements().get(DataNEssence.locate("datanessence"));
            if (advancement != null)
                hasGottenDataTablet = serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
        }

        if (!player.getInventory().contains(signalTracker) && !hasGottenDataTablet) {
            player.addItem(signalTracker);
            player.displayClientMessage(Component.translatable("block.datanessence.computer.obtain_signal_tracker"), true);
            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS);
        }
    }

    @Override
    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        if (!pPlayer.isShiftKeyDown()) {
            return 0;
        }
        return super.getDestroyProgress(pState, pPlayer, pLevel, pPos);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            default -> SHAPE_FALLOFF;
        };
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ComputerBlockEntity(pPos, pState);
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }
}
