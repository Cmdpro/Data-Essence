package com.cmdpro.datanessence.toasts;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class TierToast implements Toast {
    private static final ResourceLocation TEXTURE = DataNEssence.locate("tier_toast");
    public int tier;
    public boolean playedSound;
    public TierToast(int tier) {
        this.tier = tier;
    }
    @Override
    public Visibility render(GuiGraphics pGuiGraphics, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        pGuiGraphics.blitSprite(TEXTURE, 0, 0, this.width(), this.height());
        int i = 16776960;
        pGuiGraphics.drawString(pToastComponent.getMinecraft().font, Component.translatable("data_tablet.tier_upgrade"), 30, 7, i | -16777216, false);
        pGuiGraphics.drawString(pToastComponent.getMinecraft().font, Component.translatable("data_tablet.tier", tier), 30, 18, i | -16777216, false);
        if (!this.playedSound && pTimeSinceLastVisible > 0L) {
            this.playedSound = true;
            pToastComponent.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
        }

        return (double)pTimeSinceLastVisible >= 5000.0D * pToastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }
}
