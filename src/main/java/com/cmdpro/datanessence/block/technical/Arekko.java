package com.cmdpro.datanessence.block.technical;

import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

// Arekko Bulok-Entee, Dead Researcher whom you get your Data Tablet from.
public class Arekko extends Block implements EntityBlock {

    public Arekko(Properties properties) {
        super(properties);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        if (!player.isShiftKeyDown()) {
            return 0;
        }
        return super.getDestroyProgress(state, player, world, pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArekkoBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult result) {
        if (!world.isClientSide()) {
            ItemStack dataTablet = new ItemStack(ItemRegistry.DATA_TABLET.get(), 1);
            if (!player.getInventory().contains(dataTablet)) {
                player.addItem(dataTablet);
                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS);
                player.displayClientMessage(Component.translatable("block.datanessence.arekko.obtain_data_tablet"), true);
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
            player.displayClientMessage(Component.translatable("block.datanessence.arekko.nothing_left"), true);
        }

        return InteractionResult.sidedSuccess(world.isClientSide());
    }
}
