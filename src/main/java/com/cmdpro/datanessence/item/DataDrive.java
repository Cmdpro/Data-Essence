package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.screen.DataDriveScreen;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class DataDrive extends Item {
    public DataDrive(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        if (pStack.has(DataComponentRegistry.DATA_ID)) {
            pTooltipComponents.add(Component.translatable("item.datanessence.data_drive.loaded", Entries.entries.get(pStack.get(DataComponentRegistry.DATA_ID)).name).withStyle(ChatFormatting.GRAY));
        }
        else {
            pTooltipComponents.add(Component.translatable("item.datanessence.data_drive.empty").withStyle(ChatFormatting.GRAY));
        }
    }
    public static Entry getEntry(ItemStack stack) {
        if (stack != null) {
            if (stack.has(DataComponentRegistry.DATA_ID)) {
                return Entries.entries.get(stack.get(DataComponentRegistry.DATA_ID));
            }
        }
        return null;
    }
    public static ResourceLocation getEntryId(ItemStack stack) {
        if (stack != null) {
            if (stack.has(DataComponentRegistry.DATA_ID)) {
                return Entries.entries.get(stack.get(DataComponentRegistry.DATA_ID)).id;
            }
        }
        return null;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide) {
            Client.openScreen(pUsedHand == InteractionHand.OFF_HAND);
        }
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
    }
    public static class Client {
        public static void openScreen(boolean offhand) {
            Minecraft.getInstance().setScreen(new DataDriveScreen(Component.empty(), offhand));
        }
    }
}
