package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.computers.ClientComputerData;
import com.cmdpro.datanessence.computers.ComputerFile;
import com.cmdpro.datanessence.computers.ComputerFileType;
import com.cmdpro.datanessence.screen.datatablet.Page;
import com.google.common.base.Ascii;
import com.ibm.icu.impl.SortedSetRelation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
            if (pButton == 1) {
                file = null;
                return true;
            }
            file.getType().mouseClicked(file, pMouseX, pMouseY, pButton);
            return true;
        } else {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;
            int x2 = 8;
            int y2 = 8;
            for (ComputerFile i : ClientComputerData.clientComputerData.files) {
                ComputerFileType.ComputerFileIcon icon = i.getType().getIcon(i);
                if (pMouseX >= x+x2+2 && pMouseY >= y+y2+2 && pMouseX <= x+x2+2+icon.width && pMouseY <= y+y2+2+icon.height) {
                    file = i;
                    return true;
                }
                x2 += icon.width + 4;
                if (x2 >= imageWidth - 8) {
                    x2 = 8;
                    y2 += icon.height + 4;
                }
            }
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
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    public static final ResourceLocation TEXTURE = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/computer.png");
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (file != null) {
            file.getType().renderScreen(file, pGuiGraphics, pMouseX, pMouseY, pPartialTick, x, y);
        } else {
            int x2 = 8;
            int y2 = 8;
            for (ComputerFile i : ClientComputerData.clientComputerData.files) {
                ComputerFileType.ComputerFileIcon icon = i.getType().getIcon(i);
                pGuiGraphics.blit(icon.location, x+x2+2, y+y2+2, icon.u, icon.v, icon.width, icon.height);
                if (pMouseX >= x+x2+2 && pMouseY >= y+y2+2 && pMouseX <= x+x2+2+icon.width && pMouseY <= y+y2+2+icon.height) {
                    pGuiGraphics.fill(x+x2, y+y2, x+x2+4+icon.width, y+y2+4+icon.height, 0xaaFFFFFF);
                }
                x2 += icon.width+4;
                if (x2 >= imageWidth-8) {
                    x2 = 8;
                    y2 += icon.height+4;
                }
            }
        }
        pGuiGraphics.blit(TEXTURE, x+3, y+3, 48, 166, 4, 4);
        pGuiGraphics.blit(TEXTURE, x+3, y+imageHeight-7, 48, 170, 4, 4);
        pGuiGraphics.blit(TEXTURE, x+imageWidth-7, y+3, 52, 166, 4, 4);
        pGuiGraphics.blit(TEXTURE, x+imageWidth-7, y+imageHeight-7, 52, 170, 4, 4);
    }
}
