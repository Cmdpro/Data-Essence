package com.cmdpro.datanessence.block.storage;

import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
public class EssenceBattery extends Block implements EntityBlock {
    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
    public EssenceBattery(Properties pProperties) {
        super(pProperties);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EssenceBatteryBlockEntity(pPos, pState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof EssenceBatteryBlockEntity ent) {
            if (!pLevel.isClientSide) {
                pPlayer.displayClientMessage(Component.translatable("block.datanessence.essence_battery.amount", ent.storage.getEssence(EssenceTypeRegistry.ESSENCE.get()), ent.storage.getMaxEssence()), true);
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }


    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (be instanceof EssenceBatteryBlockEntity battery) {
            for (ItemStack drop : drops) {
                CompoundTag tag = new CompoundTag();
                tag.put("EssenceStorage", battery.storage.toNbt());
                drop.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
            }
        }

        return drops;
    }


    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.getBlockEntity(pos) instanceof EssenceBatteryBlockEntity battery) {
            CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);

            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                if (tag.contains("EssenceStorage")) {
                    battery.storage.fromNbt(tag.getCompound("EssenceStorage"));
                    battery.setChanged();
                }
            }
        }
    }
}