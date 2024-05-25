package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.PlayerFinishDataBankMinigameC2SPacket;
import com.cmdpro.datanessence.screen.databank.*;
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

import java.util.ArrayList;
import java.util.List;

public class DataBankScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/databank.png");
    public DataBankScreen(Component pTitle) {
        super(pTitle);
    }
    public static int imageWidth = 256;
    public static int imageHeight = 166;
    public double offsetX;
    public double offsetY;
    public int screenType;
    public DataBankEntry clickedEntry;
    public Minigame[] minigames;
    public int minigameProgress;
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (pButton == 0 && screenType == 0) {
            int currentTier = 1;
            int y2 = 32;
            int x2 = 4;
            for (DataBankEntry i : DataBankEntries.clientEntries.values()) {
                if (i.tier <= ClientPlayerData.getTier() && !ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                    if (i.tier != currentTier) {
                        currentTier = i.tier;
                        y2 += 32;
                        x2 = 4;
                    }
                    x2 += 30;
                    if (pMouseX >= (x2 - 10) + offsetX + x && pMouseX <= (x2 + 10) + offsetX + x) {
                        if (pMouseY >= (y2 - 10) + offsetY + y && pMouseY <= (y2 + 10) + offsetY + y) {
                            screenType = 1;
                            clickedEntry = i;
                            minigameProgress = 0;
                            List<Minigame> minigames2 = new ArrayList<>();
                            for (MinigameCreator o : i.minigames) {
                                minigames2.add(o.createMinigame());
                            }
                            minigames = minigames2.toArray(new Minigame[0]);
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            return true;
                        }
                    }
                }
            }
        }
        if (screenType == 1) {
            if (isMouseInsideMinigame(pMouseX, pMouseY)) {
                minigames[minigameProgress].mouseClicked(pMouseX - (x + (imageWidth / 4)), pMouseY - (y + (imageHeight / 3)), pButton);
                return true;
            }
        }
        if (pButton == 1 && screenType == 1) {
            screenType = 0;
            return true;
        }
        return false;
    }
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (screenType == 1) {
            if (isMouseInsideMinigame(pMouseX, pMouseY)) {
                minigames[minigameProgress].mouseDragged(pMouseX - (x + (imageWidth / 4)), pMouseY - (y + (imageHeight / 3)), pButton, pDragX, pDragY);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (screenType == 1) {
            if (isMouseInsideMinigame(pMouseX, pMouseY)) {
                minigames[minigameProgress].mouseReleased(pMouseX - (x + (imageWidth / 4)), pMouseY - (y + (imageHeight / 3)), pButton);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (screenType == 1) {
            minigames[minigameProgress].keyPressed(pKeyCode, pScanCode, pModifiers);
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (screenType == 1) {
            minigames[minigameProgress].keyReleased(pKeyCode, pScanCode, pModifiers);
            return true;
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void tick() {
        super.tick();
        if (screenType == 1) {
            minigames[minigameProgress].tick();
            if (screenType == 1) {
                if (minigames[minigameProgress].isFinished()) {
                    minigameProgress++;
                    if (minigameProgress >= minigames.length) {
                        if (!ClientPlayerUnlockedEntries.getUnlocked().contains(clickedEntry.id)) {
                            ClientPlayerUnlockedEntries.getUnlocked().add(clickedEntry.id);
                        }
                        ModMessages.sendToServer(new PlayerFinishDataBankMinigameC2SPacket(clickedEntry.id));
                        screenType = 0;
                    }
                }
            }
        }
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
        if (screenType == 0) {
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
            drawMinigame(minigames[minigameProgress], graphics, delta, mouseX, mouseY);
            graphics.blit(TEXTURE, x+(imageWidth/2)-10, y+32-10, 0, 166, 20, 20);
            graphics.renderItem(clickedEntry.icon, x+(imageWidth/2)-8, y+32-8);
        }
        graphics.disableScissor();
        super.render(graphics, mouseX, mouseY, delta);
        if (screenType == 0) {
            int currentTier = 1;
            int y2 = 32;
            int x2 = 4;
            for (DataBankEntry i : DataBankEntries.clientEntries.values()) {
                if (i.tier <= ClientPlayerData.getTier() && !ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                    if (i.tier != currentTier) {
                        currentTier = i.tier;
                        y2 += 32;
                        x2 = 4;
                    }
                    x2 += 30;
                    if (mouseX >= (x2 - 10) + offsetX + x && mouseX <= (x2 + 10) + offsetX + x) {
                        if (mouseY >= (y2 - 10) + offsetY + y && mouseY <= (y2 + 10) + offsetY + y) {
                            graphics.renderTooltip(Minecraft.getInstance().font, i.name, mouseX, mouseY);
                            break;
                        }
                    }
                }
            }
        } else if (screenType == 1) {
            if (isMouseInsideMinigame(mouseX, mouseY)) {
                graphics.renderTooltip(Minecraft.getInstance().font, minigames[minigameProgress].getTooltip(), mouseX, mouseY);
            } else if (mouseX >= x+(imageWidth/2)-10 && mouseY >=  y+32-10 && mouseX <= x+(imageWidth/2)+10 && mouseY <=  y+32+10) {
                graphics.renderTooltip(Minecraft.getInstance().font, clickedEntry.name, mouseX, mouseY);
            }
        }
    }
    public boolean isMouseInsideMinigame(double pMouseX, double pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        return pMouseX >= x+(imageWidth/4) && pMouseY >= y+(imageHeight/3) && pMouseX <= x+((imageWidth/4)*3) && pMouseY <= y+((imageHeight/5)*4);
    }
    public void drawMinigame(Minigame minigame, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.fill(x+(imageWidth/4), y+(imageHeight/3), x+((imageWidth/4)*3), y+((imageHeight/5)*4), 0xFF000000);
        pGuiGraphics.enableScissor(x+(imageWidth/4), y+(imageHeight/3), x+((imageWidth/4)*3), y+((imageHeight/5)*4));
        minigame.render(this, pGuiGraphics, pPartialTick, pMouseX, pMouseY, x+(imageWidth/4), y+(imageHeight/3));
        pGuiGraphics.disableScissor();
    }
    public void drawEntries(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int currentTier = 1;
        int y2 = 32;
        int x2 = 4;
        for (DataBankEntry i : DataBankEntries.clientEntries.values()) {
            if (i.tier <= ClientPlayerData.getTier() && !ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                if (i.tier != currentTier) {
                    currentTier = i.tier;
                    y2 += 32;
                    x2 = 4;
                }
                x2 += 30;
                pGuiGraphics.blit(TEXTURE, x + (x2 - 10) + (int) offsetX, y + (y2 - 10) + (int) offsetY, 0, 166, 20, 20);
                pGuiGraphics.renderItem(i.icon, x + (x2 - 8) + (int) offsetX, y + (y2 - 8) + (int) offsetY);
            }
        }
    }
}
