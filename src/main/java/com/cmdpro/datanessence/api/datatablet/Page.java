package com.cmdpro.datanessence.api.datatablet;

import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.gui.GuiGraphics;

public abstract class Page {

    public abstract void render(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset);

    public void renderPost(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {}

    public abstract PageSerializer getSerializer();

    public boolean onClick(DataTabletScreen screen, double pMouseX, double pMouseY, int pButton, int xOffset, int yOffset) {
        return false;
    }

    public boolean onDrag(DataTabletScreen screen, double mouseX, double mouseY, int button, double xDrag, double yDrag) {
        return false;
    }

    public int getMaxScrollY() {
        return -1;
    }
}
