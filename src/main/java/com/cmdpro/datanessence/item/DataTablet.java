package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.DataTabletUtil;
import com.cmdpro.datanessence.block.auxiliary.DataBankBlockEntity;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import com.cmdpro.datanessence.screen.DataTabletPuzzleScreen;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class DataTablet extends Item {
    public DataTablet(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            Client.openScreen();
        } else {
            if (DataTabletUtil.getTier(pPlayer) <= 0 /* && puzzle completed */) {
                //DataTabletUtil.setTier(pPlayer, 1);
            }
            //unlockStartingEntries(pPlayer);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (player != null) {
            if (stack.is(ItemRegistry.DATA_TABLET.get()) && player.isShiftKeyDown()) {
                if (!level.isClientSide) {
                    if (level.getBlockEntity(pos) instanceof DataBankBlockEntity ent) {
                        List<ResourceLocation> entries = player.getData(AttachmentTypeRegistry.UNLOCKED.get()).stream().filter((i) -> !ent.data.contains(i)).toList();
                        player.sendSystemMessage(Component.translatable("block.datanessence.player_data_bank.bind", entries.size()));
                        ent.data.addAll(entries);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.useOn(context);
    }

    public void unlockStartingEntries(Player player) {
        for (Entry i : Entries.entries.values().stream().filter((entry) -> entry.isDefault).toList()) {
            DataTabletUtil.unlockEntryAndParents(player, i.id, 0);
        }
    }

    public static class Client {
        public static void openScreen() {
            if (ClientPlayerData.getTier() < 1) {
                Minecraft.getInstance().setScreen(new DataTabletPuzzleScreen(Component.empty()));
            } else {
                Minecraft.getInstance().setScreen(new DataTabletScreen(Component.empty()));
            }
        }
    }
}
