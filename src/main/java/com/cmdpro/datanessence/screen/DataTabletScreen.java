package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.screen.datatablet.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DataTabletScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/data_tablet.png");
    public static final ResourceLocation TEXTURECRAFTING = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting.png");
    public DataTabletScreen(Component pTitle) {
        super(pTitle);
        offsetX = (imageWidth/2);
        offsetY = (imageHeight/2);
        currentTab = getSortedTabs().get(0);
    }
    public static int imageWidth = 256;
    public static int imageHeight = 166;
    public double offsetX;
    public double offsetY;
    public int screenType;
    public Entry clickedEntry;
    public int page;
    public int ticks;
    public DataTab currentTab;

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton == 0 && (screenType == 0 || screenType == 1)) {
            offsetX += pDragX;
            offsetY += pDragY;
            return true;
        }
        return false;
    }
    public boolean clickEntry(Entry entry) {
        screenType = 2;
        clickedEntry = entry;
        page = 0;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }
    public boolean clickTab(DataTab tab) {
        currentTab = tab;
        offsetX = (imageWidth/2);
        offsetY = (imageHeight/2);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (pButton == 0 && screenType == 0) {
            for (Entry i : Entries.entries.values()) {
                if (i.tab.equals(currentTab.id)) {
                    if (ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                        if (pMouseX >= ((i.x * 20) - 10) + offsetX + x && pMouseX <= ((i.x * 20) + 10) + offsetX + x) {
                            if (pMouseY >= ((i.y * 20) - 10) + offsetY + y && pMouseY <= ((i.y * 20) + 10) + offsetY + y) {
                                return clickEntry(i);
                            }
                        }
                    }
                }
            }
            int o = 0;
            for (DataTab i : getCurrentTabs()) {
                int x2 = x+(o >= 6 ? 255 : -21);
                int y2 = y+8+((o%6)*24);
                if (pMouseX >= x2 && pMouseY >= y2 && pMouseX <= x2+21 && pMouseY <= y2+20) {
                    return clickTab(i);
                }
                o++;
            }
            if (pMouseX >= (x+imageWidth)+30 && pMouseX <= (x+imageWidth)+42) {
                if (pMouseY >= y+((imageHeight/2)-20) && pMouseY <= y+((imageHeight/2)+20)) {
                    if (this.tabPage+2 < getSortedTabs().size()/6) {
                        tabPage += 1;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                }
            }
            if (pMouseX >= x-42 && pMouseX <= x-30) {
                if (pMouseY >= y+((imageHeight/2)-20) && pMouseY <= y+((imageHeight/2)+20)) {
                    if (tabPage > 0) {
                        tabPage -= 1;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                }
            }
        }
        if (pButton == 1 && screenType == 2) {
            screenType = 0;
            return true;
        }
        if (pButton == 0 && screenType == 2) {
            if (pMouseX >= (x+imageWidth)+6 && pMouseX <= (x+imageWidth)+18) {
                if (pMouseY >= y+((imageHeight/2)-20) && pMouseY <= y+((imageHeight/2)+20)) {
                    if (clickedEntry.pages.length > page+1) {
                        page += 1;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                }
            }
            if (pMouseX >= x-18 && pMouseX <= x-6) {
                if (pMouseY >= y+((imageHeight/2)-20) && pMouseY <= y+((imageHeight/2)+20)) {
                    if (page > 0) {
                        page -= 1;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ticks++;
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
    public int tabPage = 0;
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (screenType == 0) {
            int o = 0;
            for (DataTab i : getCurrentTabs()) {
                graphics.blit(TEXTURE, x+(o >= 6 ? 256 : -21), y+8+((o%6)*24), o >= 6 ? 66 : 87, 166, 21, 20);
                graphics.renderItem(i.icon, x+(o >= 6 ? 258 : -18), y+8+((o%6)*24)+2);
                o++;
            }
        }
        renderBg(graphics, delta, mouseX, mouseY);
        graphics.enableScissor(x+3, y+3, x+imageWidth-3, y+imageHeight-3);
        if (screenType == 0) {
            drawEntries(graphics, delta, mouseX, mouseY);
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 399);
            graphics.blit(TEXTURE, x+3, y+3, 48, 166, 4, 4);
            graphics.blit(TEXTURE, x+3, y+imageHeight-7, 48, 170, 4, 4);
            graphics.blit(TEXTURE, x+imageWidth-7, y+3, 52, 166, 4, 4);
            graphics.blit(TEXTURE, x+imageWidth-7, y+imageHeight-7, 52, 170, 4, 4);
            graphics.pose().popPose();
        } else if (screenType == 1) {

        } else if (screenType == 2) {
            if (clickedEntry.pages.length > 0) {
                drawPage(clickedEntry.pages[page], graphics, delta, mouseX, mouseY);
            }
        }
        graphics.disableScissor();
        super.render(graphics, mouseX, mouseY, delta);
        if (screenType == 0) {
            graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("item.datanessence.data_tablet.tier", ClientPlayerData.getTier()), x+(imageWidth/2), y-(Minecraft.getInstance().font.lineHeight+4), 0xFFc90d8b);

            if (this.tabPage+2 < getSortedTabs().size()/6) {
                graphics.blit(TEXTURE, (x + imageWidth) + 30, y + ((imageHeight / 2) - 20), 32, 166, 12, 40);
            }
            if (this.tabPage > 0) {
                graphics.blit(TEXTURE, x - 42, y + ((imageHeight / 2) - 20), 20, 166, 12, 40);
            }

            Component tooltip = null;
            for (Entry i : Entries.entries.values()) {
                if (i.tab.equals(currentTab.id)) {
                    if (ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                        if (mouseX >= ((i.x * 20) - 10) + offsetX + x && mouseX <= ((i.x * 20) + 10) + offsetX + x) {
                            if (mouseY >= ((i.y * 20) - 10) + offsetY + y && mouseY <= ((i.y * 20) + 10) + offsetY + y) {
                                tooltip = i.name;
                                break;
                            }
                        }
                    }
                }
            }
            int o = 0;
            for (DataTab i : getCurrentTabs()) {
                int x2 = x+(o >= 6 ? 255 : -21);
                int y2 = y+8+((o%6)*24);
                if (mouseX >= x2 && mouseY >= y2 && mouseX <= x2+21 && mouseY <= y2+20) {
                    tooltip = i.name;
                }
                o++;
            }
            if (tooltip != null) {
                graphics.renderTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
            }
        } else if (screenType == 2) {
            graphics.drawCenteredString(Minecraft.getInstance().font, clickedEntry.name, x+imageWidth/2, y-(Minecraft.getInstance().font.lineHeight+4), 0xFFc90d8b);
        }
    }
    public void drawPage(Page page, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        page.render(this, pGuiGraphics, pPartialTick, pMouseX, pMouseY, x, y);
        pGuiGraphics.disableScissor();
        if (this.page+1 < clickedEntry.pages.length) {
            pGuiGraphics.blit(TEXTURE, (x + imageWidth) + 6, y + ((imageHeight / 2) - 20), 32, 166, 12, 40);
        }
        if (this.page > 0) {
            pGuiGraphics.blit(TEXTURE, x - 18, y + ((imageHeight / 2) - 20), 20, 166, 12, 40);
        }
        page.renderPost(this, pGuiGraphics, pPartialTick, pMouseX, pMouseY, x, y);
        pGuiGraphics.enableScissor(x+1, y+1, x+imageWidth-1, y+imageHeight-1);
    }
    public void drawLines(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        GlStateManager._depthMask(false);
        GlStateManager._disableCull();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        Tesselator tess = RenderSystem.renderThreadTesselator();
        BufferBuilder buf = tess.getBuilder();
        RenderSystem.lineWidth(2f*(float)Minecraft.getInstance().getWindow().getGuiScale());
        for (Entry i : Entries.entries.values()) {
            if (i.tab.equals(currentTab.id)) {
                if (ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                    for (Entry o : i.getParentEntries()) {
                        if (o.tab.equals(currentTab.id)) {
                            buf.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
                            int x1 = x + ((o.x * 20)) + (int) offsetX;
                            int y1 = y + ((o.y * 20)) + (int) offsetY;
                            int x2 = x + ((i.x * 20)) + (int) offsetX;
                            int y2 = y + ((i.y * 20)) + (int) offsetY;
                            Vec2 vec1 = new Vec2(x1 >= x2 ? x1 == x2 ? 0 : 1 : -1, y1 >= y2 ? y1 == y2 ? 0 : 1 : -1);
                            Vec2 vec2 = new Vec2(x2 >= x1 ? x1 == x2 ? 0 : 1 : -1, y2 >= y1 ? y1 == y2 ? 0 : 1 : -1);
                            buf.vertex(x1, y1, 0.0D).color(201, 13, 139, 255).normal(vec1.x, vec1.y, 0).endVertex();
                            buf.vertex(x2, y2, 0.0D).color(201, 13, 139, 255).normal(vec2.x, vec2.y, 0).endVertex();
                            tess.end();
                        }
                    }
                }
            }
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GlStateManager._enableCull();
        GlStateManager._depthMask(true);
        RenderSystem.lineWidth(1);
    }
    public List<DataTab> getSortedTabs() {
        return Entries.tabs.values().stream().sorted((a, b) -> Integer.compare(b.priority, a.priority)).toList();
    }
    public List<DataTab> getCurrentTabs() {
        List<DataTab> tabs = new ArrayList<>();
        List<DataTab> sorted = getSortedTabs();
        for (int i = tabPage*6; i < Math.min(sorted.size(), (tabPage*6)+12); i++) {
            tabs.add(sorted.get(i));
        }
        return tabs;
    }
    public void drawEntries(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        drawLines(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        for (Entry i : Entries.entries.values()) {
            if (i.tab.equals(currentTab.id)) {
                if (ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                    pGuiGraphics.blit(TEXTURE, x + ((i.x * 20) - 10) + (int) offsetX, y + ((i.y * 20) - 10) + (int) offsetY, 0, 166, 20, 20);
                    pGuiGraphics.renderItem(i.icon, x + ((i.x * 20) - 8) + (int) offsetX, y + ((i.y * 20) - 8) + (int) offsetY);
                }
            }
        }
    }
}
