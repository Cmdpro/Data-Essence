package com.cmdpro.datanessence.toasts;

import com.cmdpro.datanessence.init.ItemInit;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TierToast implements Toast {
    public int tier;
    public boolean playedSound;
    public TierToast(int tier) {
        this.tier = tier;
    }
    @Override
    public Visibility render(GuiGraphics pGuiGraphics, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        pGuiGraphics.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());
        int i = 16776960;
        pGuiGraphics.drawString(pToastComponent.getMinecraft().font, Component.translatable("item.datanessence.data_tablet.tier_upgrade"), 30, 7, i | -16777216, false);
        pGuiGraphics.drawString(pToastComponent.getMinecraft().font, Component.translatable("item.datanessence.data_tablet.tier", tier), 30, 18, i | -16777216, false);
        if (!this.playedSound && pTimeSinceLastVisible > 0L) {
            this.playedSound = true;
            pToastComponent.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
        }

        pGuiGraphics.renderFakeItem(dataTabletStack, 8, 8);
        return (double)pTimeSinceLastVisible >= 5000.0D * pToastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }
    public ItemStack dataTabletStack = new ItemStack(ItemInit.DATA_TABLET.get());
}
