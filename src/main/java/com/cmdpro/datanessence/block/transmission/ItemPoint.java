package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.node.block.BaseCapabilityPoint;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class ItemPoint extends BaseCapabilityPoint {

    public ItemPoint(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Item getRequiredWire() {
        return ItemRegistry.ITEM_WIRE.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ItemPointBlockEntity(pPos, pState);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof ItemPointBlockEntity ent) {
                ItemPointBlockEntity.tick(lvl, pos, st, ent);
            }
        };
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {

        if(player.isShiftKeyDown()) {
            return new ItemStack(ItemRegistry.ITEM_WIRE.get());
        }
        return super.getCloneItemStack(state,target,level,pos,player);
    }

}
