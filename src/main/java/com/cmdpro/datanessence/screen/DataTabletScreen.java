package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
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
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jline.utils.Colors;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DataTabletScreen extends Screen {
    public static final ResourceLocation TEXTURE_MAIN = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_tablet.png");
    public static final ResourceLocation TEXTURE_PAGE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_tablet_page.png");
    public static final ResourceLocation TEXTURE_CRAFTING = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting.png");
    public static final ResourceLocation TEXTURE_MISC = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_tablet_misc.png");

    public static int imageWidth = 256;
    public static int imageHeight = 166;
    public double offsetX;
    public double offsetY;
    public int screenType;
    public Entry clickedEntry;
    public int page;
    public int ticks;
    public DataTab currentTab;

    public DataTabletScreen(Component pTitle) {
        super(pTitle);
        offsetX = (imageWidth/2);
        offsetY = (imageHeight/2);
        currentTab = getSortedTabs().get(0);
    }

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
            for (Entry entry : Entries.entries.values()) {
                if (entry.tab.equals(currentTab.id) && entry.isVisibleClient()) {
                    if (pMouseX >= ((entry.x * 20) - 10) + offsetX + x && pMouseX <= ((entry.x * 20) + 10) + offsetX + x) {
                        if (pMouseY >= ((entry.y * 20) - 10) + offsetY + y && pMouseY <= ((entry.y * 20) + 10) + offsetY + y) {
                            return clickEntry(entry);
                        }
                    }
                }
            }
            int o = 0;
            for (DataTab tab : getCurrentTabs()) {
                int x2 = x+(o >= 6 ? 255 : -21);
                int y2 = y+8+((o%6)*24);
                if (pMouseX >= x2 && pMouseY >= y2 && pMouseX <= x2+21 && pMouseY <= y2+20) {
                    return clickTab(tab);
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
                    if (clickedEntry.getPagesClient().length > page+1) {
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
            if (pMouseX >= x+1 && pMouseX <= x+imageWidth-1) {
                if (pMouseY >= y + 1 && pMouseY <= y + imageHeight - 1) {
                    return clickedEntry.pages[page].onClick(this, pMouseX, pMouseY, pButton, x, y);
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

        pGuiGraphics.blit(getTextureToUse(), x, y, 0, 0, imageWidth, imageHeight);
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

    public ResourceLocation getTextureToUse() {
        if (screenType == 2) {
            return TEXTURE_PAGE;
        } else {
            return TEXTURE_MAIN;
        }
    }

    public int tabPage = 0;

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (screenType == 0) {
            int o = 0;
            for (DataTab i : getCurrentTabs()) {
                graphics.blit(getTextureToUse(), x+(o >= 6 ? 256 : -21), y+8+((o%6)*24), o >= 6 ? 66 : 87, 166, 21, 20);
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
            graphics.blit(getTextureToUse(), x+3, y+3, 48, 166, 4, 4);
            graphics.blit(getTextureToUse(), x+3, y+imageHeight-7, 48, 170, 4, 4);
            graphics.blit(getTextureToUse(), x+imageWidth-7, y+3, 52, 166, 4, 4);
            graphics.blit(getTextureToUse(), x+imageWidth-7, y+imageHeight-7, 52, 170, 4, 4);
            graphics.pose().popPose();
        } else if (screenType == 1) {

        } else if (screenType == 2) {
            if (clickedEntry.getPagesClient().length > 0) {
                drawPage(clickedEntry.getPagesClient()[page], graphics, delta, mouseX, mouseY);
            }
        }
        graphics.disableScissor();
        if (screenType == 0) {
            graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("data_tablet.tier", ClientPlayerData.getTier()), x+(imageWidth/2), y-(Minecraft.getInstance().font.lineHeight+4), 0xFFc90d8b);

            if (this.tabPage+2 < getSortedTabs().size()/6) {
                graphics.blit(getTextureToUse(), (x + imageWidth) + 30, y + ((imageHeight / 2) - 20), 32, 166, 12, 40);
            }
            if (this.tabPage > 0) {
                graphics.blit(getTextureToUse(), x - 42, y + ((imageHeight / 2) - 20), 20, 166, 12, 40);
            }

            Component tooltip = null;
            for (Entry i : Entries.entries.values()) {
                if (i.tab.equals(currentTab.id)) {
                    if (i.isVisibleClient()) {
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
        if (this.page+1 < clickedEntry.getPagesClient().length) {
            pGuiGraphics.blit(getTextureToUse(), (x + imageWidth) + 6, y + ((imageHeight / 2) - 20), 12, 166, 12, 40);
        }
        if (this.page > 0) {
            pGuiGraphics.blit(getTextureToUse(), x - 18, y + ((imageHeight / 2) - 20), 0, 166, 12, 40);
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
        RenderSystem.lineWidth(2f*(float)Minecraft.getInstance().getWindow().getGuiScale());
        long time = System.currentTimeMillis() / 50;
        for (Entry i : Entries.entries.values()) {
            if (i.tab.equals(currentTab.id)) {
                if (i.isVisibleClient()) {
                    for (Entry o : i.getParentEntries()) {
                        if (o.tab.equals(currentTab.id)) {
                            BufferBuilder buf = tess.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
                            int x1 = x + ((o.x * 20)) + (int) offsetX;
                            int y1 = y + ((o.y * 20)) + (int) offsetY;
                            int x2 = x + ((i.x * 20)) + (int) offsetX;
                            int y2 = y + ((i.y * 20)) + (int) offsetY;
                            Vec2 vec = new Vec2(x1-x2, y1-y2).normalized();
                            buf.addVertex(x1, y1, 0.0F).setColor(201, 13, 139, 255).setNormal(vec.x, vec.y, 0);
                            buf.addVertex(x2, y2, 0.0F).setColor(201, 13, 139, 255).setNormal(vec.x, vec.y, 0);
                            BufferUploader.drawWithShader(buf.buildOrThrow());
                        }
                    }
                }
            }
        }
        for (Entry destinationEntry : Entries.entries.values()) {
            if (destinationEntry.tab.equals(currentTab.id) && destinationEntry.isVisibleClient()) {
                for (Entry sourceEntry : destinationEntry.getParentEntries()) {
                    if (sourceEntry.tab.equals(currentTab.id)) {
                        int sourceX = x + (sourceEntry.x * 20) + (int) offsetX;
                        int sourceY = y + (sourceEntry.y * 20) + (int) offsetY;
                        int destinationX = x + (destinationEntry.x * 20) + (int) offsetX;
                        int destinationY = y + (destinationEntry.y * 20) + (int) offsetY;

                        Vec3 origin = new Vec3(sourceX, sourceY, 0);
                        Vec3 line = new Vec3(origin.x - destinationX, origin.y - destinationY, 0);
                        int lineSegments = (int) Math.ceil(line.length() / 1);
                        int activeSegment = lineSegments-(int)(time % lineSegments);
                        Vec3 segmentIteration = new Vec3(line.x / lineSegments, line.y / lineSegments, line.z / lineSegments);

                        for (int i = lineSegments; i >= 0; i--) {
                            double lx = origin.x();
                            double ly = origin.y();
                            origin = origin.subtract(segmentIteration);
                            Vec2 normal = new Vec2((float) (origin.x - lx), (float) (origin.y - ly)).normalized();
                            float blendSize = 10;
                            float blend = Math.clamp(0f, 1f, (blendSize-Math.abs((float)i-(float)activeSegment))/blendSize);
                            int color = blendColors(new Color(45, 6, 61), new Color(255, 120, 201), blend).getRGB();

                            BufferBuilder builder = tess.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
                            builder.addVertex((float) lx, (float) ly, 0).setColor(color).setNormal(normal.x, normal.y, 0);
                            builder.addVertex((float) origin.x, (float) origin.y, 0).setColor(color).setNormal(normal.x, normal.y, 0);
                            BufferUploader.drawWithShader(builder.buildOrThrow());
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
    private Color blendColors(Color color1, Color color2, float blend) {
        Vector4f vec4Color1 = new Vector4f(color1.getRed(), color1.getBlue(), color1.getGreen(), color1.getAlpha());
        Vector4f vec4Color2 = new Vector4f(color2.getRed(), color2.getBlue(), color2.getGreen(), color1.getAlpha());
        Vector4f vec4Blended = vec4Color1.lerp(vec4Color2, blend);
        return new Color((int)vec4Blended.x, (int)vec4Blended.y, (int)vec4Blended.z, (int)vec4Blended.w);
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
                if (i.isVisibleClient()) {
                    pGuiGraphics.blit(TEXTURE_MAIN, x + ((i.x * 20) - 10) + (int) offsetX, y + ((i.y * 20) - 10) + (int) offsetY, 0, 166, 20, 20);
                    pGuiGraphics.renderItem(i.icon, x + ((i.x * 20) - 8) + (int) offsetX, y + ((i.y * 20) - 8) + (int) offsetY);
                }
            }
        }
    }
}
