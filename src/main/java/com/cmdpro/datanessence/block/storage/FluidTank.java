package com.cmdpro.datanessence.block.storage;

import com.cmdpro.datanessence.api.util.TextUtil;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import net.neoforged.neoforge.fluids.SimpleFluidContent;


import java.util.List;

public class FluidTank extends TransparentBlock implements EntityBlock {
    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 16, 16);

    public FluidTank(Properties pProperties) {
        super(pProperties);
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
        return new FluidTankBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,
                                                                  BlockState state,
                                                                  BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        if (type == BlockEntityRegistry.FLUID_TANK.get()) {
            return (lvl, pos, st, be) ->
                    FluidTankBlockEntity.serverTick(lvl, pos, st, (FluidTankBlockEntity) be);
        }
        return null;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (be instanceof FluidTankBlockEntity tankBE) {
            var handler = tankBE.getFluidHandler();
            FluidStack fluid = handler.getFluidInTank(0);

            if (!fluid.isEmpty() && fluid.getAmount() > 0) {
                SimpleFluidContent content = SimpleFluidContent.copyOf(fluid);

                for (ItemStack drop : drops) {
                    drop.set(DataComponentRegistry.STORED_FLUID, content);
                }
            }
        }

        return drops;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.getBlockEntity(pos) instanceof FluidTankBlockEntity tankBE) {
            SimpleFluidContent stored = stack.get(DataComponentRegistry.STORED_FLUID);
            if (stored != null && !stored.isEmpty()) {
                FluidStack fs = stored.copy();
                if (!fs.isEmpty() && fs.getAmount() > 0) {
                    tankBE.getFluidHandler().fill(fs, IFluidHandler.FluidAction.EXECUTE);
                    tankBE.setChanged();
                }
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof FluidTankBlockEntity ent) {
            if (!pLevel.isClientSide) {
                FluidStack fluid = (FluidStack) ent.getFluidHandler().getFluidInTank(0);
                if (fluid.getAmount() > 0) {
                    pPlayer.displayClientMessage(
                            Component.translatable(
                                    "block.datanessence.fluid_tank.amount",
                                    TextUtil.getFluidText(fluid.getAmount()),
                                    TextUtil.getFluidText(ent.getFluidHandler().getTankCapacity(0)),
                                    fluid.getHoverName()
                            ),
                            true
                    );
                } else {
                    pPlayer.displayClientMessage(
                            Component.translatable("block.datanessence.fluid_tank.nothing"),
                            true
                    );
                }
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos,
                                              Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof FluidTankBlockEntity ent) {
            if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, ent.getFluidHandler())) {
                return ItemInteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                Item.TooltipContext context,
                                List<Component> tooltip,
                                TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        SimpleFluidContent stored = stack.get(DataComponentRegistry.STORED_FLUID);

        if (stored != null && !stored.isEmpty()) {
            FluidStack fluid = stored.copy();

            int amount = fluid.getAmount();
            Component fluidName = fluid.getHoverName();

            int capacity = 16000;

            tooltip.add(Component.translatable(
                    "tooltip.datanessence.fluid_tank.contains",
                    TextUtil.getFluidText(amount),
                    TextUtil.getFluidText(capacity),
                    fluidName
            ));
        } else {
            tooltip.add(Component.translatable("tooltip.datanessence.fluid_tank.empty"));
        }
    }
}
