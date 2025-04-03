package com.cmdpro.datanessence.minigames;

import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.databank.Minigame;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Vector2i;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ColorMixingMinigame extends Minigame {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_bank_minigames.png");

    public Color targetColor;
    public Color startColor;
    public List<ColorMixingMinigameCreator.ColorMixingMinigameSerializer.ColorManipulation> manipulations;
    public int maxManipulations;
    public int colorLevels;
    public int currentColorLevel;
    public ColorMixingMinigame(List<ColorMixingMinigameCreator.ColorMixingMinigameSerializer.ColorManipulation> manipulations, Color startColor, int maxManipulations, int colorLevels) {
        this.targetColor = startColor;
        for (ColorMixingMinigameCreator.ColorMixingMinigameSerializer.ColorManipulation i : manipulations) {
            targetColor = ColorUtil.mixColorsSubtractive(targetColor, i.color, i.getIntensity());
        }
        this.startColor = startColor;
        this.maxManipulations = maxManipulations;
        this.colorLevels = colorLevels;
        this.manipulations = new ArrayList<>();
        currentColorLevel = colorLevels;
    }
    @Override
    public boolean isFinished() {
        Color currentColor = getCurrentColor();
        float[] targetHsv = Color.RGBtoHSB(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue(), null);
        float[] currentHsv = Color.RGBtoHSB(getCurrentColor().getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
        int[] targetHsvInt = new int[3];
        int[] currentHsvInt = new int[3];
        for (int i = 0; i < 3; i++) {
            targetHsvInt[i] = (int)(targetHsv[i]*100f);
            currentHsvInt[i] = (int)(currentHsv[i]*100f);
        }
        return currentHsvInt[0] == targetHsvInt[0] && currentHsvInt[1] == targetHsvInt[1] && currentHsvInt[2] == targetHsvInt[2];
    }
    public Color getCurrentColor() {
        Color color = startColor;
        for (ColorMixingMinigameCreator.ColorMixingMinigameSerializer.ColorManipulation i : manipulations) {
            color = ColorUtil.mixColorsSubtractive(color, i.color, i.getIntensity());
        }
        return color;
    }
    @Override
    public void render(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y) {
        Color currentColor = getCurrentColor();
        float[] color = RenderSystem.getShaderColor().clone();
        RenderSystem.setShaderColor(((float)currentColor.getRed())/255f, ((float)currentColor.getGreen())/255f, ((float)currentColor.getBlue())/255f, 1f);
        RenderSystem.enableBlend();
        pGuiGraphics.blit(TEXTURE, x+((150/2)-24), y+((150/2)-24), 208, 0, 48, 48);
        RenderSystem.setShaderColor(0f, 1f, 1f, 1f);
        pGuiGraphics.blit(TEXTURE, x+((150/2)+24)+16, y+((150/2)-28), 198, 48, 16, 16);
        RenderSystem.setShaderColor(1f, 0f, 1f, 1f);
        pGuiGraphics.blit(TEXTURE, x+((150/2)+24)+16, y+((150/2)-28)+20, 198, 48, 16, 16);
        RenderSystem.setShaderColor(1f, 1f, 0f, 1f);
        pGuiGraphics.blit(TEXTURE, x+((150/2)+24)+16, y+((150/2)-28)+40, 198, 48, 16, 16);
        RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
        RenderSystem.disableBlend();
        pGuiGraphics.blit(TEXTURE, x+((150/2)-16), y+16, 192, 64, 32, 14);
        pGuiGraphics.fill(x+((150/2)-6), y+17, x+((150/2)-6)+12, y+17+12, targetColor.getRGB());
        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, "Target", x+(150/2), y+5, 0xFFFFFFFF);
        float[] targetHsv = Color.RGBtoHSB(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue(), null);
        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, (int)(targetHsv[0]*100) + "% H | " + (int)(targetHsv[1]*100) + "% S | " + (int)(targetHsv[2]*100) + "% V", x+(150/2), y+17+16, 0xFFFFFFFF);
        float[] currentHsv = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, (int)(currentHsv[0]*100) + "% H | " + (int)(currentHsv[1]*100) + "% S | " + (int)(currentHsv[2]*100) + "% V", x+(150/2), y+((150/2)+30), 0xFFFFFFFF);

        int undoX = x+((150/2)-48);
        int undoY = y+((150/2)-20);
        int resetX = x+((150/2)-48);
        int resetY = y+((150/2)+4);
        if (pMouseX >= undoX && pMouseY >= undoY && pMouseX <= undoX+16 && pMouseY <= undoY+16) {
            pGuiGraphics.blit(TEXTURE, undoX, undoY, 240, 64, 16, 16);
        } else {
            pGuiGraphics.blit(TEXTURE, undoX, undoY, 224, 64, 16, 16);
        }
        if (pMouseX >= resetX && pMouseY >= resetY && pMouseX <= resetX+16 && pMouseY <= resetY+16) {
            pGuiGraphics.blit(TEXTURE, resetX, resetY, 240, 48, 16, 16);
        } else {
            pGuiGraphics.blit(TEXTURE, resetX, resetY, 224, 48, 16, 16);
        }
        pGuiGraphics.hLine(x+(150/2)-32, x+(150/2)+32, y+((150/2)+48), 0xFF2621a5);
        int sliderX = x+((150/2)-32)+(int)((((float)currentColorLevel/(float)colorLevels)*64f)-2.5f);
        int sliderY = y+((150/2)+48)-8;
        if (pMouseX >= sliderX && pMouseY >= sliderY && pMouseX <= sliderX+5 && pMouseY <= sliderY+16) {
            pGuiGraphics.blit(TEXTURE, sliderX, sliderY, 219, 48, 5, 16);
        } else {
            pGuiGraphics.blit(TEXTURE, sliderX, sliderY, 214, 48, 5, 16);
        }
    }

    @Override
    public void mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int sliderX = ((150/2)-32)+(int)((((float)currentColorLevel/(float)colorLevels)*64f)-2.5f);
        int sliderY = ((150/2)+48)-8;
        if (pMouseX >= sliderX && pMouseY >= sliderY && pMouseX <= sliderX+5 && pMouseY <= sliderY+16) {
            draggingSlider = true;
        }
        int undoX = ((150/2)-48);
        int undoY = ((150/2)-20);
        int resetX = ((150/2)-48);
        int resetY = ((150/2)+4);
        if (pMouseX >= undoX && pMouseY >= undoY && pMouseX <= undoX+16 && pMouseY <= undoY+16) {
            if (!manipulations.isEmpty()) {
                manipulations.removeLast();
            }
            Client.click();
        }
        if (pMouseX >= resetX && pMouseY >= resetY && pMouseX <= resetX+16 && pMouseY <= resetY+16) {
            manipulations.clear();
            Client.click();
        }
        if (manipulations.size()+1 <= maxManipulations) {
            int colorX = ((150/2)+24)+16;
            int cyanY = ((150/2)-28);
            int magentaY = ((150/2)-28)+20;
            int yellowY = ((150/2)-28)+40;
            if (pMouseX >= colorX && pMouseY >= cyanY && pMouseX <= colorX+16 && pMouseY <= cyanY+16) {
                Client.paint();
                manipulations.add(new ColorMixingMinigameCreator.ColorMixingMinigameSerializer.ColorManipulation(colorLevels, currentColorLevel, Color.CYAN));
            }
            if (pMouseX >= colorX && pMouseY >= magentaY && pMouseX <= colorX+16 && pMouseY <= magentaY+16) {
                Client.paint();
                manipulations.add(new ColorMixingMinigameCreator.ColorMixingMinigameSerializer.ColorManipulation(colorLevels, currentColorLevel, Color.MAGENTA));
            }
            if (pMouseX >= colorX && pMouseY >= yellowY && pMouseX <= colorX+16 && pMouseY <= yellowY+16) {
                Client.paint();
                manipulations.add(new ColorMixingMinigameCreator.ColorMixingMinigameSerializer.ColorManipulation(colorLevels, currentColorLevel, Color.YELLOW));
            }
        }
    }
    public boolean draggingSlider;

    @Override
    public void mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (draggingSlider) {
            int sliderMinX = ((150 / 2) - 32);
            int distFromMin = (int)pMouseX-sliderMinX;
            if (distFromMin < 0) {
                currentColorLevel = 0;
            } else if (distFromMin > 64) {
                currentColorLevel = colorLevels;
            } else {
                currentColorLevel = Math.round(colorLevels*((float)distFromMin/64f));
            }
        }
    }

    @Override
    public void mouseReleased(double pMouseX, double pMouseY, int pButton) {
        draggingSlider = false;
    }

    public class Client {
        public static void click() {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1f, 1f));
        }
        public static void paint() {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.INK_SAC_USE, 1f, 1f));
        }
    }
}
