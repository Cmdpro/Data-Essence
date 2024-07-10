package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.computers.ClientComputerData;
import com.cmdpro.datanessence.computers.ComputerFile;
import com.cmdpro.datanessence.computers.ComputerFileType;
import com.cmdpro.datanessence.screen.datatablet.Page;
import com.google.common.base.Ascii;
import com.ibm.icu.impl.SortedSetRelation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforgespi.Environment;

public class ComputerScreen extends Screen {
    public ComputerScreen(Component pTitle) {
        super(pTitle);
    }
    public ComputerFile file;
    public static int imageWidth = 256;
    public static int imageHeight = 166;
    @Override
    public void tick() {
        super.tick();
        if (file != null) {
            file.getType().tick(file);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (file != null) {
            file.getType().mouseClicked(file, pMouseX, pMouseY, pButton);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (file != null) {
            file.getType().mouseDragged(file, pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (file != null) {
            file.getType().mouseReleased(file, pMouseX, pMouseY, pButton);
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (file != null) {
            file.getType().keyPressed(file, pKeyCode, pScanCode, pModifiers);
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        if (file == null) {
            super.onClose();
        } else {
            file = null;
        }
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (file != null) {
            file.getType().keyReleased(file, pKeyCode, pScanCode, pModifiers);
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (file != null) {
            file.getType().renderScreen(file, pGuiGraphics, pMouseX, pMouseY, pPartialTick, x, y);
        } else {
            int x2 = 0;
            int y2 = 0;
            for (ComputerFile i : ClientComputerData.clientComputerData.files) {
                ComputerFileType.ComputerFileIcon icon = i.getType().getIcon(i);
                pGuiGraphics.blit(icon.location, x2, y2, );
            }
        }
    }
}
