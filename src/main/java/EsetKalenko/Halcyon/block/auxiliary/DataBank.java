package EsetKalenko.Halcyon.block.auxiliary;

import EsetKalenko.Halcyon.Halcyon;
import EsetKalenko.Halcyon.api.block.BaseDataBankBlock;
import EsetKalenko.Halcyon.api.util.DataTabletUtil;
import EsetKalenko.Halcyon.data.datatablet.Entries;
import EsetKalenko.Halcyon.data.datatablet.Entry;
import EsetKalenko.Halcyon.registry.AttachmentTypeRegistry;
import EsetKalenko.Halcyon.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DataBank extends BaseDataBankBlock implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public DataBank(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 16, 16);

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(ItemRegistry.DATA_TABLET.get())) {
            if (!level.isClientSide) {
                if (level.getBlockEntity(pos) instanceof DataBankBlockEntity ent) {
                    List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
                    List<ResourceLocation> entries = Arrays.stream(getEntries(state, level, pos, player, hitResult)).filter((i) -> !unlocked.contains(i)).toList();
                    player.sendSystemMessage(Component.translatable("block.halcyon.player_data_bank.receive", entries.size()));
                    for (ResourceLocation i : entries) {
                        Entry entry = Entries.entries.get(i);

                        if (entry == null) {
                            Halcyon.LOGGER.error("[HALCYON] Tried to load non-existent data entry \"{}\" from Data Bank!?", i);
                            continue;
                        }

                        DataTabletUtil.unlockEntryAndParents(player, i, entry.completionStages.size());
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        return InteractionResult.PASS;
    }

    @Override
    public ResourceLocation[] getEntries(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof DataBankBlockEntity ent) {
            return ent.data.toArray(new ResourceLocation[0]);
        }
        return new ResourceLocation[0];
    }

    @Override
    public boolean isOkayToOpen(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof DataBankBlockEntity ent) {
            return super.isOkayToOpen(pState, pLevel, pPos, pPlayer, pHitResult);
        }
        return false;
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
        return SHAPE;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DataBankBlockEntity(pPos, pState);
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
