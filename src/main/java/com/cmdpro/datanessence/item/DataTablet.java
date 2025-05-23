package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.DataTabletUtil;
import com.cmdpro.datanessence.screen.DataTabletScreen;
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
            if (DataTabletUtil.getTier(pPlayer) <= 0) {
                DataTabletUtil.setTier(pPlayer, 1);
            }

            unlockStartingEntries(pPlayer);
        }
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
    }

    public void unlockStartingEntries(Player player) {
        DataTabletUtil.unlockEntry(player, DataNEssence.locate("basics/fabricator"), false);
        DataTabletUtil.unlockEntry(player, DataNEssence.locate("basics/essence_redirector"), false);
        DataTabletUtil.unlockEntry(player, DataNEssence.locate("basics/data_tablet"), false);
        DataTabletUtil.unlockEntry(player, DataNEssence.locate("basics/structures"), false);
        DataTabletUtil.unlockEntry(player, DataNEssence.locate("basics/locator"), false);
        DataTabletUtil.unlockEntry(player, DataNEssence.locate("tools/decorative_blocks"), false);
    }

    public static class Client {
        public static void openScreen() {
            Minecraft.getInstance().setScreen(new DataTabletScreen(Component.empty()));
        }
    }
}
