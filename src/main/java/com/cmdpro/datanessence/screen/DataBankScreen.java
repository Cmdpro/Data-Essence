package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntry;
import com.cmdpro.datanessence.screen.databank.Minigame;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.screen.datatablet.Page;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class DataBankScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/databank.png");
    public DataBankScreen(Component pTitle) {
        super(pTitle);
        offsetX = (imageWidth/2);
        offsetY = (imageHeight/2);
    }
    public static int imageWidth = 256;
    public static int imageHeight = 166;
    public double offsetX;
    public double offsetY;
    public int screenType;
    public DataBankEntry clickedEntry;
    public int minigameProgress;
/*
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton == 0 && (screenType == 0 || screenType == 1)) {
            offsetX += pDragX;
            offsetY += pDragY;
            return true;
        }
        return false;
    }*/
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (pButton == 0 && screenType == 0) {
            int currentTier = 1;
            int y2 = 8;
            int x2 = x+4;
            for (DataBankEntry i : DataBankEntries.entries.values()) {
                if (i.tier <= ClientPlayerData.getTier()) {
                    if (i.tier != currentTier) {
                        currentTier = i.tier;
                        y2 += 32;
                        x2 = x+4;
                    }
                    if (pMouseX >= ((x2 * 20) - 10) + offsetX + x && pMouseX <= ((x2* 20) + 10) + offsetX + x) {
                        if (pMouseY >= ((y2 * 20) - 10) + offsetY + y && pMouseY <= ((y2 * 20) + 10) + offsetY + y) {
                            screenType = 1;
                            clickedEntry = i;
                            minigameProgress = 0;
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            return true;
                        }
                    }
                }
            }
        }
        if (pButton == 1 && screenType == 1) {
            screenType = 0;
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }


    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        if (screenType != 2) {
            super.onClose();
        } else {
            screenType = 0;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        renderBg(graphics, delta, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.enableScissor(x+3, y+3, x+imageWidth-3, y+imageHeight-3);
        if (screenType == 0) {
            drawEntries(graphics, delta, mouseX, mouseY);
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 399);
            graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("item.datanessence.datatablet.tier", ClientPlayerData.getTier()), x+(imageWidth/2), y+8, 0xFFc90d8b);
            graphics.blit(TEXTURE, x+3, y+3, 20, 166, 4, 4);
            graphics.blit(TEXTURE, x+3, y+imageHeight-7, 20, 170, 4, 4);
            graphics.blit(TEXTURE, x+imageWidth-7, y+3, 24, 166, 4, 4);
            graphics.blit(TEXTURE, x+imageWidth-7, y+imageHeight-7, 24, 170, 4, 4);
            graphics.pose().popPose();
        } else if (screenType == 1) {
            drawMinigame(clickedEntry.minigames[minigameProgress], graphics, delta, mouseX, mouseY);
            graphics.blit(TEXTURE, x+(imageWidth/3), y+32-10, 0, 166, 20, 20);
            graphics.renderItem(clickedEntry.icon, x+(imageWidth/3), y+32-8);
            graphics.blit(TEXTURE, x+(imageWidth/3)-30, y+((imageHeight/3)*2)+32, 20, 174, 60, 12);
        }
        graphics.disableScissor();
        super.render(graphics, mouseX, mouseY, delta);
        if (screenType == 0) {
            for (Entry i : Entries.entries.values()) {
                if (ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                    if (mouseX >= ((i.x * 20) - 10) + offsetX + x && mouseX <= ((i.x * 20) + 10) + offsetX + x) {
                        if (mouseY >= ((i.y * 20) - 10) + offsetY + y && mouseY <= ((i.y * 20) + 10) + offsetY + y) {
                            graphics.renderTooltip(Minecraft.getInstance().font, i.name, mouseX, mouseY);
                            break;
                        }
                    }
                }
            }
        }
    }
    public void drawMinigame(Minigame minigame, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.fill(x+(imageWidth/3), y+(imageHeight/3), x-((imageWidth/3)*2), y-((imageHeight/3)*2), 0xFF000000);
        pGuiGraphics.enableScissor(x+(imageWidth/3), y+(imageHeight/3), x-((imageWidth/3)*2), y-((imageHeight/3)*2));
        minigame.render(this, pGuiGraphics, pPartialTick, pMouseX, pMouseY, x+(imageWidth/3), y+(imageHeight/3));
        pGuiGraphics.disableScissor();
    }
    public void drawEntries(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int currentTier = 1;
        int y2 = 8;
        int x2 = x+4;
        for (DataBankEntry i : DataBankEntries.entries.values()) {
            if (i.tier <= ClientPlayerData.getTier()) {
                if (i.tier != currentTier) {
                    currentTier = i.tier;
                    y2 += 32;
                    x2 = x+4;
                }
                pGuiGraphics.blit(TEXTURE, x + (x2 - 10) + (int) offsetX, y + (y2 - 10) + (int) offsetY, 0, 166, 20, 20);
                pGuiGraphics.renderItem(i.icon, x + (x2 - 8) + (int) offsetX, y + (y2 - 8) + (int) offsetY);
                x2 += 20;
            }
        }
    }
}
