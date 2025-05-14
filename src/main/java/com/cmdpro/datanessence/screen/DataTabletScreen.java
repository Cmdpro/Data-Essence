package com.cmdpro.datanessence.screen;

import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.SpecialConditionHandler;
import com.cmdpro.datanessence.api.datatablet.Page;
import com.cmdpro.datanessence.data.datatablet.DataTab;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DataTabletScreen extends Screen {
    public static final ResourceLocation TEXTURE_FRAME = DataNEssence.locate("textures/gui/data_tablet_frame.png");
    public static final ResourceLocation TEXTURE_MAIN = DataNEssence.locate("textures/gui/data_tablet.png");
    public static final ResourceLocation TEXTURE_PAGE = DataNEssence.locate("textures/gui/data_tablet_page.png");
    public static final ResourceLocation TEXTURE_CRAFTING = DataNEssence.locate("textures/gui/data_tablet_crafting.png");
    public static final ResourceLocation TEXTURE_CRAFTING2 = DataNEssence.locate("textures/gui/data_tablet_crafting2.png");
    public static final ResourceLocation TEXTURE_MISC = DataNEssence.locate("textures/gui/data_tablet_misc.png");

    public static int imageWidth = 256;
    public static int imageHeight = 166;
    public static ResourceLocation savedEntry;
    public static int savedPage;
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
        if (savedEntry != null) {
            for (Entry i : Entries.entries.values()) {
                if (i.id.equals(savedEntry)) {
                    clickedEntry = i;
                    screenType = 2;
                    page = savedPage;
                    break;
                }
            }
        }
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
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.get(), 1.0F));
        screenType = 2;
        clickedEntry = entry;
        page = 0;
        return true;
    }

    public boolean clickTab(DataTab tab) {
        currentTab = tab;
        offsetX = (imageWidth/2);
        offsetY = (imageHeight/2);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.get(), 1.0F));
        return true;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (pButton == 0 && screenType == 0) {
            if (pMouseX >= x && pMouseY >= y && pMouseX <= x+imageWidth && pMouseY <= y+imageHeight) {
                for (Entry entry : Entries.entries.values()) {
                    if (entry.tab.equals(currentTab.id) && entry.isVisibleClient()) {
                        if (pMouseX >= ((entry.x * 20) - 10) + offsetX + x && pMouseX <= ((entry.x * 20) + 10) + offsetX + x) {
                            if (pMouseY >= ((entry.y * 20) - 10) + offsetY + y && pMouseY <= ((entry.y * 20) + 10) + offsetY + y) {
                                return clickEntry(entry);
                            }
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
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.get(), 1.0F));
                        return true;
                    }
                }
            }
            if (pMouseX >= x-42 && pMouseX <= x-30) {
                if (pMouseY >= y+((imageHeight/2)-20) && pMouseY <= y+((imageHeight/2)+20)) {
                    if (tabPage > 0) {
                        tabPage -= 1;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.get(), 1.0F));
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
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.get(), 1.0F));
                        return true;
                    }
                }
            }
            if (pMouseX >= x-18 && pMouseX <= x-6) {
                if (pMouseY >= y+((imageHeight/2)-20) && pMouseY <= y+((imageHeight/2)+20)) {
                    if (page > 0) {
                        page -= 1;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.get(), 1.0F));
                        return true;
                    }
                }
            }
            if (pMouseX >= x+1 && pMouseX <= x+imageWidth-1) {
                if (pMouseY >= y + 1 && pMouseY <= y + imageHeight - 1) {
                    return clickedEntry.pages[page].onClick(this, pMouseX, pMouseY, pButton, x, y);
                }
            }
            if (pMouseX >= x+imageWidth && pMouseX <= x+imageWidth+17 && pMouseY >= y+10 && pMouseY <= y+10+21) {
                if (Screen.hasShiftDown()) {
                    savedEntry = null;
                    savedPage = 0;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.get(), 1.0F));
                }
                else {
                    savedEntry = clickedEntry.id;
                    savedPage = page;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.get(), 1.0F));
                    super.onClose();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ticks++;
        if (SpecialConditionHandler.isAprilFools()) {
            battery -= ((1f / 20f) / 5f) * (Math.clamp(0, 1, (battery / 100f) * 3));///30f;
            if (battery < 1) {
                battery = 1;
            }
        }
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
                int shift = 0;
                int x2 = x+(o >= 6 ? 255 : -21);
                int y2 = y+8+((o%6)*24);
                if (mouseX >= x2 && mouseY >= y2 && mouseX <= x2+21 && mouseY <= y2+20) {
                    shift = 40;
                }
                graphics.blit(getTextureToUse(), x+(o >= 6 ? 256 : -21), y+8+((o%6)*24), o >= 6 ? 66 : 87, 166+shift, 21, 20);
                graphics.renderItem(i.icon, x+(o >= 6 ? 258 : -18), y+8+((o%6)*24)+2);
                o++;
            }
        }

        // this is done with four, as both the top and bottom halves of the frame differ in color, and the "handle" needs to be of reasonable length.
        // TODO fix this
//        graphics.blitWithBorder(TEXTURE_FRAME, 32, 32, 0, 0, 340, 55, 72, 29, 9); // top half
//        graphics.blitWithBorder(TEXTURE_FRAME, 32, 32+55, 0, 30, 340, 25, 72, 5, 9); // top handle
//        graphics.blitWithBorder(TEXTURE_FRAME, 32, 32+80, 0, 35, 340, 25, 72, 5, 9); // bottom handle
//        graphics.blitWithBorder(TEXTURE_FRAME, 32, 32+105, 0, 39, 340, 105, 72, 29, 9); // bottom half

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
                int shift = 0;
                if (mouseX >= (x+imageWidth)+6 && mouseX <= (x+imageWidth)+18) {
                    if (mouseY >= y + ((imageHeight / 2) - 20) && mouseY <= y + ((imageHeight / 2) + 20)) {
                        shift = 40;
                    }
                }
                graphics.blit(getTextureToUse(), (x + imageWidth) + 30, y + ((imageHeight / 2) - 20), 32, 166+shift, 12, 40);
            }
            if (this.tabPage > 0) {
                int shift = 0;
                if (mouseX >= x-18 && mouseX <= x-6) {
                    if (mouseY >= y+((imageHeight/2)-20) && mouseY <= y+((imageHeight/2)+20)) {
                        shift = 40;
                    }
                }
                graphics.blit(getTextureToUse(), x - 42, y + ((imageHeight / 2) - 20), 20, 166, 12+shift, 40);
            }

            List<FormattedCharSequence> tooltip = null;
            if (mouseX >= x && mouseY >= y && mouseX <= x+imageWidth && mouseY <= y+imageHeight) {
                for (Entry entry : Entries.entries.values()) {
                    if (entry.tab.equals(currentTab.id)) {
                        if (entry.isVisibleClient()) {
                            if (mouseX >= ((entry.x * 20) - 10) + offsetX + x && mouseX <= ((entry.x * 20) + 10) + offsetX + x) {
                                if (mouseY >= ((entry.y * 20) - 10) + offsetY + y && mouseY <= ((entry.y * 20) + 10) + offsetY + y) {
                                    if (entry.isIncompleteClient()) {
                                        Component progressionRequirement = Component.translatable("tooltip.datanessence.progression_requirement").copy().withStyle(ChatFormatting.ITALIC).withColor(0xFFff61ca);
                                        tooltip = entry.flavor.equals(Component.empty())
                                                ? List.of(entry.name.getVisualOrderText(), progressionRequirement.getVisualOrderText())
                                                : List.of(entry.name.getVisualOrderText(), entry.flavor.copy().withStyle(ChatFormatting.ITALIC).withColor(EssenceTypeRegistry.ESSENCE.get().getColor()).getVisualOrderText(), progressionRequirement.getVisualOrderText());
                                    } else
                                        tooltip = entry.flavor.equals(Component.empty())
                                            ? List.of(entry.name.getVisualOrderText())
                                            : List.of(entry.name.getVisualOrderText(), entry.flavor.copy().withStyle(ChatFormatting.ITALIC).withColor(EssenceTypeRegistry.ESSENCE.get().getColor()).getVisualOrderText());
                                    break;
                                }
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
                    tooltip = List.of(i.name.getVisualOrderText());
                }
                o++;
            }
            if (tooltip != null) {
                graphics.renderTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
            }
        } else if (screenType == 2) {
            graphics.drawCenteredString(Minecraft.getInstance().font, clickedEntry.name, x+imageWidth/2, y-(Minecraft.getInstance().font.lineHeight+4), 0xFFc90d8b);
            if (mouseX >= x+imageWidth && mouseX <= x+imageWidth+17 && mouseY >= y+10 && mouseY <= y+10+21) {
                Component tooltip = !Screen.hasShiftDown() ? Component.translatable("tooltip.datanessence.save_and_exit") : Component.translatable("tooltip.datanessence.save_and_exit_sneak");
                graphics.renderTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
                graphics.blit(TEXTURE_MISC, x + imageWidth, y + 10, 38, 21, 17, 21);
            } else {
                graphics.blit(TEXTURE_MISC, x + imageWidth, y + 10, 38, 0, 17, 21);
            }
        }
        if (SpecialConditionHandler.isAprilFools()) {
            graphics.blit(TEXTURE_MISC, x + 5, y + 5, 16, 38, 17, 8);
            int batteryWidth = 11;
            if (battery <= 99f) {
                batteryWidth = 8;
            }
            if (battery <= (100f/3f)*2f) {
                batteryWidth = 5;
            }
            if (battery <= (100f/3f)) {
                batteryWidth = 2;
            }
            graphics.blit(TEXTURE_MISC, x + 8, y + 7, 16, 46, batteryWidth, 8);
            graphics.drawString(Minecraft.getInstance().font, (int)Math.ceil(battery) + "%", x + 24, y + 5, 0xff96b5);
        }
    }
    public static float battery = 100;
    public void drawPage(Page page, GuiGraphics graphics, float pPartialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        page.render(this, graphics, pPartialTick, mouseX, mouseY, x, y);
        graphics.disableScissor();
        if (this.page+1 < clickedEntry.getPagesClient().length) {
            int shift = 0;
            if (mouseX >= (x+imageWidth)+6 && mouseX <= (x+imageWidth)+18) {
                if (mouseY >= y + ((imageHeight / 2) - 20) && mouseY <= y + ((imageHeight / 2) + 20)) {
                    shift = 40;
                }
            }
            graphics.blit(getTextureToUse(), (x + imageWidth) + 6, y + ((imageHeight / 2) - 20), 12, 166+shift, 12, 40);
        }
        if (this.page > 0) {
            int shift = 0;
            if (mouseX >= x-18 && mouseX <= x-6) {
                if (mouseY >= y+((imageHeight/2)-20) && mouseY <= y+((imageHeight/2)+20)) {
                    shift = 40;
                }
            }
            graphics.blit(getTextureToUse(), x - 18, y + ((imageHeight / 2) - 20), 0, 166+shift, 12, 40);
        }
        page.renderPost(this, graphics, pPartialTick, mouseX, mouseY, x, y);
        graphics.enableScissor(x+1, y+1, x+imageWidth-1, y+imageHeight-1);
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
                            float blendSize = 8;
                            float blend = Math.clamp(0f, 1f, (blendSize-Math.abs((float)i-(float)activeSegment))/blendSize);
                            int color = ColorUtil.blendColors(new Color(17, 20, 38, 255), new Color(70, 216, 252, 255), blend).getRGB();
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
                    int entryShift = 0;
                    int incompleteShift = 0;
                    if (pMouseX >= x && pMouseY >= y && pMouseX <= x+imageWidth && pMouseY <= y+imageHeight) {
                        if (pMouseX >= ((i.x * 20) - 10) + offsetX + x && pMouseX <= ((i.x * 20) + 10) + offsetX + x) {
                            if (pMouseY >= ((i.y * 20) - 10) + offsetY + y && pMouseY <= ((i.y * 20) + 10) + offsetY + y) {
                                entryShift = 40;
                                incompleteShift = 10;
                            }
                        }
                    }
                    pGuiGraphics.blit(TEXTURE_MAIN, x + ((i.x * 20) - 10) + (int) offsetX, y + ((i.y * 20) - 10) + (int) offsetY, 0, 166+entryShift, 20, 20);
                    pGuiGraphics.renderItem(i.icon, x + ((i.x * 20) - 8) + (int) offsetX, y + ((i.y * 20) - 8) + (int) offsetY);

                    if (i.isIncompleteClient()) {
                        pGuiGraphics.blit(TEXTURE_MAIN, x + ((i.x * 20) + 5) + (int) offsetX, y + ((i.y * 20) + 5) + (int) offsetY, 0, 186+incompleteShift, 11, 10);
                    }
                }
            }
        }
    }
}
