package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.screen.MusicDiscPlayerMenu;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MusicDiscPlayer extends Item {
    public MusicDiscPlayer(Properties pProperties) {
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
                    return new MusicDiscPlayerMenu(pContainerId, pPlayerInventory, pPlayer.getItemInHand(pUsedHand));
                }
            }, (buf) -> {
                buf.writeInt(pPlayer.getInventory().findSlotMatchingItem(pPlayer.getItemInHand(pUsedHand)));
            });
        }
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        boolean remove = true;
        ItemContainerContents handler = pStack.get(DataComponents.CONTAINER);
        if (handler != null) {
            if (handler.getSlots() > 0) {
                JukeboxPlayable playable = handler.getStackInSlot(0).get(DataComponents.JUKEBOX_PLAYABLE);
                if (playable != null) {
                    JukeboxSong song = pLevel.registryAccess().registry(Registries.JUKEBOX_SONG).get().get(playable.song().key());
                    if (song != null) {
                        pStack.set(DataComponentRegistry.PLAYING_MUSIC, song.soundEvent().getKey());
                        remove = false;
                    }
                }
            }
        }
        if (remove) {
            pStack.remove(DataComponentRegistry.PLAYING_MUSIC);
        }
    }
}
