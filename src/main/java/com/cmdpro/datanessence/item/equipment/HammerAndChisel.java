package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.registry.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class HammerAndChisel extends Item {

    public HammerAndChisel(Properties properties) {
        super(properties);
    }

    // Mark this block with the chisel.
    public boolean chiselBlock(Level world, BlockPos target, ItemStack stack, Player player) {
        BlockState targetState = world.getBlockState(target);

        if (targetState.is(TagRegistry.Blocks.HAMMER_AND_CHISEL_COLLECTABLE)) {
            stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
            // somehow save the block coord to the tool
            return true;
        }

        return false;
    }

    // Destroy this block with the hammer.
    public boolean hammerBlock(Level world, BlockPos target, ItemStack stack, Player player) {
        BlockState targetState = world.getBlockState(target);

        if (targetState.is(TagRegistry.Blocks.HAMMER_AND_CHISEL_COLLECTABLE)) {
            stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
            world.destroyBlock(target, true);
            return true;
        }
        return false;
    }

    public void playSound(Level world, BlockPos target) {
        world.playSound(null, target, SoundEvents.ANVIL_BREAK, SoundSource.PLAYERS, 1f, 2f);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (world.isClientSide())
            return InteractionResult.SUCCESS;

        // TODO make it so you need to click twice on the same block to destroy it, rather than just once.
        if (hammerBlock(world, pos, context.getItemInHand(), player)) {
            playSound(world, pos);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
}
