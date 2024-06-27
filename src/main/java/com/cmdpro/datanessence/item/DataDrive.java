package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.screen.DataDriveScreen;
import com.cmdpro.datanessence.screen.DataTabletScreen;
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
import org.apache.commons.compress.harmony.pack200.CPString;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DataDrive extends Item {
    public DataDrive(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if (pStack.hasTag()) {
            if (pStack.getTag().contains("dataId")) {
                pTooltipComponents.add(Component.translatable("item.datanessence.data_drive.loaded", Entries.entries.get(ResourceLocation.tryParse(pStack.getTag().getString("dataId"))).name).withStyle(ChatFormatting.GRAY));
            }
        }
        else {
            pTooltipComponents.add(Component.translatable("item.datanessence.data_drive.empty").withStyle(ChatFormatting.GRAY));
        }
    }
    public static Entry getEntry(ItemStack stack) {
        if (stack != null) {
            if (stack.hasTag()) {
                if (stack.getTag().contains("dataId")) {
                    return Entries.entries.get(ResourceLocation.tryParse(stack.getTag().getString("dataId")));
                }
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
