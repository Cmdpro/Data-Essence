package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DataTablet extends Item {
    public DataTablet(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide) {
            Client.openScreen();
        } else {
            if (DataNEssenceUtil.DataTabletUtil.getTier(pPlayer) <= 0) {
                DataNEssenceUtil.DataTabletUtil.setTier(pPlayer, 1);
            }
            DataNEssenceUtil.DataTabletUtil.unlockEntry(pPlayer, new ResourceLocation(DataNEssence.MOD_ID, "fabricator"));
            DataNEssenceUtil.DataTabletUtil.unlockEntry(pPlayer, new ResourceLocation(DataNEssence.MOD_ID, "datatablet"));
        }
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
    }
    public static class Client {
        public static void openScreen() {
            Minecraft.getInstance().setScreen(new DataTabletScreen(Component.empty()));
        }
    }
}
