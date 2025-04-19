package com.cmdpro.datanessence.toasts;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.data.datatablet.Entry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;

public class CriticalDataToast implements Toast {
    private static final ResourceLocation TEXTURE = DataNEssence.locate("critical_data_toast");
    public Entry entry;
    public boolean playedSound;
    public CriticalDataToast(Entry entry) {
        this.entry = entry;
    }
    @Override
    public Visibility render(GuiGraphics pGuiGraphics, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        pGuiGraphics.blitSprite(TEXTURE, 0, 0, this.width(), this.height());
        List<FormattedCharSequence> list = pToastComponent.getMinecraft().font.split(entry.name, 125);
        int i = 16776960;
        if (list.size() == 1) {
            pGuiGraphics.drawString(pToastComponent.getMinecraft().font, Component.translatable("data_tablet.critical_data_unlocked"), 30, 7, i | -16777216, false);
            pGuiGraphics.drawString(pToastComponent.getMinecraft().font, list.get(0), 30, 18, -1, false);
        } else {
            int j = 1500;
            float f = 300.0F;
            if (pTimeSinceLastVisible < 1500L) {
                int k = Mth.floor(Mth.clamp((float)(1500L - pTimeSinceLastVisible) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                pGuiGraphics.drawString(pToastComponent.getMinecraft().font, Component.translatable("data_tablet.critical_data_unlocked"), 30, 11, i | k, false);
            } else {
                int i1 = Mth.floor(Mth.clamp((float)(pTimeSinceLastVisible - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                int l = this.height() / 2 - list.size() * 9 / 2;
                for(FormattedCharSequence formattedcharsequence : list) {
                    pGuiGraphics.drawString(pToastComponent.getMinecraft().font, formattedcharsequence, 30, l, 16777215 | i1, false);
                    l += 9;
                }
            }
        }
        if (!this.playedSound && pTimeSinceLastVisible > 0L) {
            this.playedSound = true;
            pToastComponent.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.CRITICAL_DATA_UNLOCKED.get(), 1.0F, 1.0F));
        }

        pGuiGraphics.renderFakeItem(entry.icon, 8, 8);
        return (double)pTimeSinceLastVisible >= 5000.0D * pToastComponent.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}
