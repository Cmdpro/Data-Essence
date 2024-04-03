package com.cmdpro.datanessence.screen.datatablet;

import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.gui.GuiGraphics;

public abstract class Page {
    public abstract void render(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset);
    public void renderPost(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {}
    public abstract PageSerializer getSerializer();
}
