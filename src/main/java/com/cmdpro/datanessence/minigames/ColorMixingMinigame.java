package com.cmdpro.datanessence.minigames;

import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.databank.Minigame;
import com.cmdpro.datanessence.screen.DataBankScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Vector2i;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ColorMixingMinigame extends Minigame {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_bank_minigames.png");

    public Color targetColor;
    public Color startColor;
    public List<ColorMixingMinigameCreator.ColorMixingMinigameSerializer.ColorManipulation> manipulations;
    public int maxManipulations;
    public int colorLevels;
    public ColorMixingMinigame(List<ColorMixingMinigameCreator.ColorMixingMinigameSerializer.ColorManipulation> manipulations, Color startColor, int maxManipulations, int colorLevels) {
        this.targetColor = startColor;
        for (ColorMixingMinigameCreator.ColorMixingMinigameSerializer.ColorManipulation i : manipulations) {
            targetColor = ColorUtil.mixColorsSubtractive(targetColor, i.getColor());
        }
        this.startColor = startColor;
        this.maxManipulations = maxManipulations;
        this.colorLevels = colorLevels;
        this.manipulations = new ArrayList<>();
    }
    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void render(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y) {

    }

    @Override
    public void mouseClicked(double pMouseX, double pMouseY, int pButton) {

    }

    @Override
    public void mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {

    }

    @Override
    public void mouseReleased(double pMouseX, double pMouseY, int pButton) {

    }

    public class Client {

    }
}
