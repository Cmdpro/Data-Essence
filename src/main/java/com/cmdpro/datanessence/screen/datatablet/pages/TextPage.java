package com.cmdpro.datanessence.screen.datatablet.pages;

import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.cmdpro.datanessence.screen.datatablet.Page;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.serializers.TextPageSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.nio.charset.MalformedInputException;
import java.util.List;

public class TextPage extends Page {
    public TextPage(Component text) {
        this.text = text;
    }
    public Component text;
    public int textYOffset() {
        return 0;
    }
    @Override
    public void render(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {
        List<FormattedCharSequence> text = Minecraft.getInstance().font.split(this.text, DataTabletScreen.imageWidth - 8);
        int offsetY = 0;
        for (FormattedCharSequence i : text) {
            pGuiGraphics.drawString(Minecraft.getInstance().font, i, xOffset + 4, yOffset + 4 + offsetY + textYOffset(), 0xFFFFFFFF);
            offsetY += Minecraft.getInstance().font.lineHeight+2;
        }
    }

    @Override
    public PageSerializer getSerializer() {
        return TextPageSerializer.INSTANCE;
    }
}
