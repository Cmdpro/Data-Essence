package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.item.INodeUpgrade;
import com.cmdpro.datanessence.block.transmission.ItemPointBlockEntity;
import com.cmdpro.datanessence.screen.FilterNodeUpgradeMenu;
import com.cmdpro.datanessence.screen.MusicDiscPlayerMenu;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FilterNodeUpgrade extends Item implements INodeUpgrade {
    public FilterNodeUpgrade(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            pPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.empty();
                }
                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer2) {
                    return new FilterNodeUpgradeMenu(pContainerId, pPlayerInventory, pPlayer.getItemInHand(pUsedHand));
                }
            }, (buf) -> {
                buf.writeInt(pPlayer.getInventory().findSlotMatchingItem(pPlayer.getItemInHand(pUsedHand)));
            });
        }
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
    }

    @Override
    public Object getValue(ItemStack upgrade, ResourceLocation id, Object originalValue, BlockEntity node) {
        if (id.equals(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "allowed_itemstacks"))) {
            ItemContainerContents handler = upgrade.get(DataComponents.CONTAINER);
            if (handler != null) {
                if (handler.getSlots() > 0) {
                    if (handler.getStackInSlot(0).isEmpty()) {
                        return originalValue;
                    } else {
                        return List.of(handler.getStackInSlot(0));
                    }
                }
            }
        }
        return originalValue;
    }

    @Override
    public Type getType() {
        return Type.UNIQUE;
    }
}
