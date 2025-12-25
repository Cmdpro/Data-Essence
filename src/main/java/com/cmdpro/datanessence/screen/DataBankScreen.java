package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.databank.Minigame;
import com.cmdpro.datanessence.api.databank.MinigameCreator;
import com.cmdpro.datanessence.data.databank.DataBankEntries;
import com.cmdpro.datanessence.data.databank.DataBankEntry;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.c2s.PlayerFinishDataBankMinigame;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DataBankScreen extends Screen {
    public static final ResourceLocation TEXTURE = DataNEssence.locate("textures/gui/data_bank.png");
    public DataBankScreen(Component pTitle) {
        super(pTitle);
        minigameCompletionWait = -1;
    }
    public static int imageWidth = 256;
    public static int imageHeight = 166;
    public double offsetX;
    public double offsetY;
    public int screenType;
    public DataBankEntry clickedEntry;
    public Minigame[] minigames;
    public int minigameProgress;
    public int minigameCompletionWait;
    public HashMap<ResourceLocation, Integer> downloading = new HashMap<>();
    public static final int downloadTime = 20;
    public boolean isUnlocked(Entry entry) {
        return downloading.containsKey(entry.id) || entry.isUnlockedClient();
    }
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (pButton == 0 && screenType == 0) {
            int currentTier = -1;
            int y2 = 0;
            int x2 = 4;
            List<DataBankEntry> entries = new ArrayList<>(DataBankEntries.clientEntries.values().stream().toList());
            entries.sort(Comparator.comparingInt((a) -> a.tier));
            for (DataBankEntry i : entries) {
                boolean isDownloading = downloading.containsKey(i.id);
                if (isDownloading || i.tier <= ClientPlayerData.getTier() && !ClientPlayerUnlockedEntries.getUnlocked().contains(i.entry) && !ClientPlayerUnlockedEntries.getIncomplete().containsKey(i.entry)) {
                    Entry entry = Entries.entries.get(i.entry);
                    if (entry == null || (!isUnlocked(entry) && !isDownloading)) {
                        continue;
                    }
                    if (i.tier != currentTier) {
                        currentTier = i.tier;
                        y2 += 32;
                        x2 = 4;
                    }
                    x2 += 30;
                    if (isDownloading) {
                        continue;
                    }
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
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.value(), 1.0F));
                            return true;
                        }
                    }
                }
            }
        }
        if (screenType == 1) {
            if (minigameCompletionWait == -1) {
                if (isMouseInsideMinigame(pMouseX, pMouseY)) {
                    if (minigameProgress < minigames.length) {
                        double mouseX = pMouseX - (x + (imageWidth / 2) - 75);
                        double mouseY = pMouseY - (y + (imageHeight / 2) - 75);
                        minigames[minigameProgress].mouseClicked(mouseX, mouseY, pButton);
                        return true;
                    }
                }
            }
        }
        if (pButton == 1 && screenType == 1) {
            if (minigameCompletionWait == -1) {
                screenType = 0;
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (screenType == 0) {
            if (pButton == 0) {
                offsetX += pDragX;
                offsetY += pDragY;
                if (offsetX > 0) {
                    offsetX = 0;
                }
                if (offsetY > 0) {
                    offsetY = 0;
                }
                return true;
            }
            return false;
        }
        if (screenType == 1) {
            if (minigameCompletionWait == -1) {
                if (isMouseInsideMinigame(pMouseX, pMouseY) && isMouseInsideMinigame(pMouseX + pDragX, pMouseY + pDragY)) {
                    if (minigameProgress < minigames.length) {
                        double mouseX = pMouseX - (x + (imageWidth / 2) - 75);
                        double mouseY = pMouseY - (y + (imageHeight / 2) - 75);
                        minigames[minigameProgress].mouseDragged(mouseX, mouseY, pButton, pDragX, pDragY);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (screenType == 1) {
            if (minigameCompletionWait == -1) {
                if (isMouseInsideMinigame(pMouseX, pMouseY)) {
                    if (minigameProgress < minigames.length) {
                        double mouseX = pMouseX - (x + (imageWidth / 2) - 75);
                        double mouseY = pMouseY - (y + (imageHeight / 2) - 75);
                        minigames[minigameProgress].mouseReleased(mouseX, mouseY, pButton);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (screenType == 1) {
            if (minigameCompletionWait == -1) {
                if (minigameProgress < minigames.length) {
                    minigames[minigameProgress].keyPressed(pKeyCode, pScanCode, pModifiers);
                }
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (screenType == 1) {
            if (minigameCompletionWait == -1) {
                if (minigameProgress < minigames.length) {
                    minigames[minigameProgress].keyReleased(pKeyCode, pScanCode, pModifiers);
                }
            }
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void tick() {
        super.tick();
        if (screenType == 1) {
            if (minigameProgress >= minigames.length) {
                Entry entry = Entries.entries.get(clickedEntry.entry);
                if (!entry.completionStages.isEmpty()) {
                    if (!ClientPlayerUnlockedEntries.getIncomplete().containsKey(clickedEntry.entry)) {
                        ClientPlayerUnlockedEntries.getIncomplete().put(clickedEntry.entry, 0);
                    }
                } else {
                    if (!ClientPlayerUnlockedEntries.getUnlocked().contains(clickedEntry.entry)) {
                        ClientPlayerUnlockedEntries.getUnlocked().add(clickedEntry.entry);
                    }
                }
                downloading.put(clickedEntry.id, downloadTime);
                ModMessages.sendToServer(new PlayerFinishDataBankMinigame(clickedEntry.id));
                screenType = 0;
            } else {
                minigames[minigameProgress].tick();
                if (minigames[minigameProgress].isFinished()) {
                    if (minigameCompletionWait == -1) {
                        minigameCompletionWait = 40;
                    }
                }
                if (minigameCompletionWait > 0) {
                    minigameCompletionWait--;
                    if (minigameCompletionWait <= 0) {
                        minigameCompletionWait = -1;
                        minigameProgress++;
                    }
                }
            }
        } else {
            minigameCompletionWait = -1;
            minigames = null;
        }
        for (Map.Entry<ResourceLocation, Integer> i : downloading.entrySet().stream().toList()) {
            if (i.getValue()-1 <= 0) {
                downloading.remove(i.getKey());
            } else {
                downloading.put(i.getKey(), i.getValue()-1);
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
        if (minigameCompletionWait == -1) {
            if (screenType == 0) {
                super.onClose();
            } else {
                screenType = 0;
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        renderBg(graphics, delta, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // needed this to not be scissored
        if (screenType == 1 && minigameProgress < minigames.length) {
            // TODO make this only appear for a few seconds instead of all the time maybe?, and make the color cycle
            var minigameName = Component.translatable(minigames[minigameProgress].getLocalizationKey());
            graphics.drawCenteredString(Minecraft.getInstance().font, minigameName, x+(imageWidth/2), y-8, 0xffffff);
        }

        graphics.enableScissor(x+3, y+3, x+imageWidth-3, y+imageHeight-3);
        if (screenType == 0) {
            drawEntries(graphics, delta, mouseX, mouseY);
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 399);
            graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("data_tablet.tier", ClientPlayerData.getTier()), x+(imageWidth/2), y+8, 0xFFc90d8b);
            graphics.blit(TEXTURE, x+3, y+3, 20, 166, 4, 4);
            graphics.blit(TEXTURE, x+3, y+imageHeight-7, 20, 170, 4, 4);
            graphics.blit(TEXTURE, x+imageWidth-7, y+3, 24, 166, 4, 4);
            graphics.blit(TEXTURE, x+imageWidth-7, y+imageHeight-7, 24, 170, 4, 4);
            graphics.pose().popPose();
        } else if (screenType == 1) {
            if (minigameProgress < minigames.length) {
                drawMinigame(minigames[minigameProgress], graphics, delta, mouseX, mouseY);
            }
            graphics.blit(TEXTURE, x+26-10, y+26-10, 0, 166, 20, 20);
            graphics.renderItem(clickedEntry.icon, x+26-8, y+26-8);

            if (minigameCompletionWait > 0) {
                Color fade = new Color(1f, 1f, 1f, 1f-((float)minigameCompletionWait/40f));
                Color fade2 = new Color(0f, 0f, 0f, 1f-((float)minigameCompletionWait/40f));
                Component completed = Component.translatable("data_tablet.databank_minigame_completed");
                int textWidth = Minecraft.getInstance().font.width(completed);
                int textHeight = Minecraft.getInstance().font.lineHeight;
                graphics.fillGradient(x+(imageWidth/2)-(textWidth+5), y+(imageHeight/2)-(textHeight+10), x+(imageWidth/2)+(textWidth+5), y+(imageHeight/2), 0x00000000, fade2.getRGB());
                graphics.fillGradient(x+(imageWidth/2)-(textWidth+5), y+(imageHeight/2), x+(imageWidth/2)+(textWidth+5), y+(imageHeight/2)+(textHeight+10), fade2.getRGB(), 0x00000000);
                graphics.drawCenteredString(Minecraft.getInstance().font, completed, x+(imageWidth/2), y+(imageHeight/2)-(textHeight/2), fade.getRGB());
            }
        }
        graphics.disableScissor();
        if (screenType == 0) {
            int currentTier = -1;
            int y2 = 0;
            int x2 = 4;
            List<DataBankEntry> entries = new ArrayList<>(DataBankEntries.clientEntries.values().stream().toList());
            entries.sort(Comparator.comparingInt((a) -> a.tier));

            for (DataBankEntry i : entries) {
                if (downloading.containsKey(i.id) || i.tier <= ClientPlayerData.getTier() && !ClientPlayerUnlockedEntries.getUnlocked().contains(i.entry) && !ClientPlayerUnlockedEntries.getIncomplete().containsKey(i.entry)) {
                    Entry entry = Entries.entries.get(i.entry);
                    if (entry == null || (!isUnlocked(entry) && !downloading.containsKey(i.id))) {
                        continue;
                    }
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
            boolean anyUnfinished = false;
            for (DataBankEntry i : entries) {
                if (downloading.containsKey(i.id) || i.tier <= ClientPlayerData.getTier() && !ClientPlayerUnlockedEntries.getUnlocked().contains(i.entry) && !ClientPlayerUnlockedEntries.getIncomplete().containsKey(i.entry)) {
                    Entry entry = Entries.entries.get(i.entry);
                    if (entry == null || isUnlocked(entry)) {
                        anyUnfinished = true;
                    }
                }
            }

            if (!anyUnfinished) {
                graphics.blit(TEXTURE, (width / 2) - 23, (height / 2) - 33, 39, 166, 46, 46);
                graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("data_tablet.databank_no_entries_left"), width / 2, height / 2, new Color(255, 150, 181).getRGB());
            }

        } else if (screenType == 1) {
            if (isMouseInsideMinigame(mouseX, mouseY)) {
                if (minigameProgress < minigames.length) {
                    graphics.renderTooltip(Minecraft.getInstance().font, minigames[minigameProgress].getTooltip(), mouseX, mouseY);
                }
            } else if (mouseX >= x+26-10 && mouseY >= y+26-10 && mouseX <= x+26+10 && mouseY <= y+26+10) {
                graphics.renderTooltip(Minecraft.getInstance().font, clickedEntry.name, mouseX, mouseY);
            }
        }
    }

    public boolean isMouseInsideMinigame(double pMouseX, double pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        return pMouseX >= x+(imageWidth/2)-75 && pMouseY >= y+(imageHeight/2)-75 && pMouseX <= x+(imageWidth/2)+75 && pMouseY <= y+(imageHeight/2)+75;
    }

    public void drawMinigame(Minigame minigame, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.fill(x+(imageWidth/2)-75, y+(imageHeight/2)-75, x+(imageWidth/2)+75, y+(imageHeight/2)+75, 0xFF000000);
        pGuiGraphics.enableScissor(x+(imageWidth/2)-75, y+(imageHeight/2)-75, x+(imageWidth/2)+75, y+(imageHeight/2)+75);
        minigame.render(this, pGuiGraphics, pPartialTick, pMouseX, pMouseY, x+(imageWidth/2)-75, y+(imageHeight/2)-75);
        pGuiGraphics.disableScissor();
    }

    public void drawEntries(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int currentTier = -1;
        int y2 = 0;
        int x2 = 4;
        HashMap<Integer, Integer> tiers = new HashMap<>();
        List<DataBankEntry> entries = new ArrayList<>(DataBankEntries.clientEntries.values().stream().toList());
        entries.sort(Comparator.comparingInt((a) -> a.tier));

        for (DataBankEntry i : entries) {
            if (downloading.containsKey(i.id) || i.tier <= ClientPlayerData.getTier() && !ClientPlayerUnlockedEntries.getUnlocked().contains(i.entry) && !ClientPlayerUnlockedEntries.getIncomplete().containsKey(i.entry)) {
                Entry entry = Entries.entries.get(i.entry);
                if (entry == null || !isUnlocked(entry)) {
                    continue;
                }
                if (i.tier != currentTier) {
                    currentTier = i.tier;
                    y2 += 32;
                    x2 = 4;
                }
                x2 += 30;

                var vOffset = entry.critical ? 206 : 166;
                pGuiGraphics.blit(TEXTURE, x + (x2 - 10) + (int) offsetX, y + (y2 - 10) + (int) offsetY, 0, vOffset, 20, 20);
                pGuiGraphics.renderItem(i.icon, x + (x2 - 8) + (int) offsetX, y + (y2 - 8) + (int) offsetY);

                if (downloading.containsKey(i.id)) {
                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().translate(0, 0, 200);
                    BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
                    float radius = 8;
                    Vec2 center = new Vec2((x + x2 + (int) offsetX), (y + y2 + (int) offsetY));
                    Color color = new Color(0.75F, 0F, 0.75F, 0.5f);
                    RenderSystem.enableBlend();
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    builder.addVertex(pGuiGraphics.pose().last(), center.x, center.y, 0).setColor(color.getRGB());
                    for (int deg = (int)(360*(1f-Math.clamp(((float)downloading.get(i.id)-pPartialTick)/(float)downloadTime, 0f, 1f))); deg >= 0; deg--) {
                        float rad = (deg - 90) / 180F * (float) Math.PI;
                        builder.addVertex(pGuiGraphics.pose().last(), center.x + Mth.cos(rad) * radius, center.y + Mth.sin(rad) * radius, 0).setColor(color.getRGB());
                    }
                    builder.addVertex(pGuiGraphics.pose().last(), center.x, center.y, 0).setColor(color.getRGB());
                    BufferUploader.drawWithShader(builder.buildOrThrow());
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    pGuiGraphics.blit(TEXTURE, (int)center.x-8, (int)center.y-8, 85, 166, 16, 16);
                    RenderSystem.disableBlend();
                    pGuiGraphics.pose().popPose();
                }

                if (tiers.containsKey(i.tier)) {
                    tiers.put(i.tier, tiers.get(i.tier)+1);
                } else {
                    tiers.put(i.tier, 0);
                }
            }
        }

        int o = 1;
        for (Map.Entry<Integer, Integer> i : tiers.entrySet()) {
            pGuiGraphics.blit(TEXTURE, x+4+(int)offsetX, y+((32*o)-14)+(int)offsetY, 28, 166, 3, 28);
            int width = (i.getValue()*30)+30;
            for (int p = 0; p < width+13; p++) {
                pGuiGraphics.blit(TEXTURE, x + 7 + p + (int) offsetX, y + ((32 * o) - 14) + (int) offsetY, 30, 166, 1, 28);
            }
            pGuiGraphics.blit(TEXTURE, x+20+width+(int)offsetX, y+((32*o)-14)+(int)offsetY, 30, 166, 3, 28);
            pGuiGraphics.blit(TEXTURE, x+18+(int)offsetX, y+((32*o)-14)+(int)offsetY, 30, 166, 3, 28);
            pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, i.getKey().toString(), x+13+(int)offsetX, y+(int)offsetY+(32*o)-(Minecraft.getInstance().font.lineHeight/2), 0xFFc90d8b);
            o++;
        }
    }
}
