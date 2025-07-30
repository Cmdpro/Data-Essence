package com.cmdpro.datanessence.api.databank;

import com.cmdpro.datanessence.screen.DataBankScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public abstract class Minigame {
    public abstract void render(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y);
    public List<FormattedCharSequence> getTooltip() { return new ArrayList<>(); }
    public void mouseClicked(double pMouseX, double pMouseY, int pButton) {}
    public void mouseReleased(double pMouseX, double pMouseY, int pButton) {}
    public void mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {}
    public void tick() {}
    public void keyPressed(int pKeyCode, int pScanCode, int pModifiers) {}
    public void keyReleased(int pKeyCode, int pScanCode, int pModifiers) {}
    public abstract boolean isFinished();

    public abstract String getLocalizationKey();
}
