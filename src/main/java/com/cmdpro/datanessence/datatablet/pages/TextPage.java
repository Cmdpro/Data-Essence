package com.cmdpro.datanessence.datatablet.pages;

import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.cmdpro.datanessence.api.datatablet.Page;
import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.datatablet.pages.serializers.TextPageSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.FormattedBidiReorder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
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
        List<FormattedText> text = Minecraft.getInstance().font.getSplitter().splitLines(component, DataTabletScreen.imageWidth - 8, Style.EMPTY);
        int offsetY = 0;
        for (FormattedText i : text) {
            int x = xOffset + 4;
            if (rtl) {
                x = xOffset+((DataTabletScreen.imageWidth - 4)-Minecraft.getInstance().font.width(i));
            }
            boolean shouldRtl = true;
            if (!rtl) {
                shouldRtl = Language.getInstance().isDefaultRightToLeft();
            }
            pGuiGraphics.drawString(Minecraft.getInstance().font, FormattedBidiReorder.reorder(i, shouldRtl), x, yOffset + 4 + offsetY + textYOffset(), 0xFFFFFFFF);
            offsetY += Minecraft.getInstance().font.lineHeight+2;
        }
    }

    @Override
    public PageSerializer getSerializer() {
        return TextPageSerializer.INSTANCE;
    }
}
