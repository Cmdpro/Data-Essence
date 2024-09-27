package com.cmdpro.datanessence.screen.datatablet.pages;

import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.cmdpro.datanessence.api.datatablet.Page;
import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.serializers.TextPageSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TextPage extends Page {
    public TextPage(Component text, boolean rtl) {
        this.text = text;
        this.rtl = rtl;
    }
    public Component text;
    public boolean rtl;
    public int textYOffset() {
        return 0;
    }
    @Override
    public void render(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {
        MutableComponent component = this.text.copy();
        List<FormattedCharSequence> text = Minecraft.getInstance().font.split(component, DataTabletScreen.imageWidth - 8);
        int offsetY = 0;
        for (FormattedCharSequence i : text) {
            int x = xOffset + 4;
            if (rtl) {
                x = xOffset+((DataTabletScreen.imageWidth - 4)-Minecraft.getInstance().font.width(i));
            }
            pGuiGraphics.drawString(Minecraft.getInstance().font, i, x, yOffset + 4 + offsetY + textYOffset(), 0xFFFFFFFF);
            offsetY += Minecraft.getInstance().font.lineHeight+2;
        }
    }

    @Override
    public PageSerializer getSerializer() {
        return TextPageSerializer.INSTANCE;
    }
}
