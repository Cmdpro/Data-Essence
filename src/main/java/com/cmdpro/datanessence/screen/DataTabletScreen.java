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
import org.joml.Vector2i;

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
    public boolean scrolling;

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
        if ( screenType == 2 )
            return clickedEntry.getPagesClient().get(page).onDrag(this, pMouseX, pMouseY, pButton, pDragX, pDragY);

        if (pButton == 0 && (screenType == 0 || screenType == 1)) {
            offsetX += pDragX;
            offsetY += pDragY;
            return true;
        }
        return false;
    }

    public boolean clickEntry(Entry entry, int button) {
        if (button != 0) {
            return false;
        }
        return clickEntry(entry);
    }
    public boolean clickEntry(Entry entry) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.value(), 1.0F));
        screenType = 2;
        clickedEntry = entry;
        scrollbarPixels = 0;
        page = 0;
        return true;
    }

    public boolean clickTab(DataTab tab) {
        currentTab = tab;
        offsetX = (imageWidth/2);
        offsetY = (imageHeight/2);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.value(), 1.0F));
        return true;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (screenType == 0) {
            if (pMouseX >= x && pMouseY >= y && pMouseX <= x+imageWidth && pMouseY <= y+imageHeight) {
                for (Entry entry : Entries.entries.values()) {
                    if (entry.tab.equals(currentTab.id) && isEntryUnlocked(entry)) {
                        Vector2i entryPosition = getEntryPosition(entry);
                        if (pMouseX >= ((entryPosition.x * 20) - 10) + offsetX + x && pMouseX <= ((entryPosition.x * 20) + 10) + offsetX + x) {
                            if (pMouseY >= ((entryPosition.y * 20) - 10) + offsetY + y && pMouseY <= ((entryPosition.y * 20) + 10) + offsetY + y) {
                                return clickEntry(entry, pButton);
                            }
                        }
                    }
                }
            }
            if (pButton == 0) {
                int o = 0;
                for (DataTab tab : getCurrentTabs()) {
                    int x2 = x + (o >= 6 ? 255 : -21);
                    int y2 = y + 8 + ((o % 6) * 24);
                    if (pMouseX >= x2 && pMouseY >= y2 && pMouseX <= x2 + 21 && pMouseY <= y2 + 20) {
                        return clickTab(tab);
                    }
                    o++;
                }
                if (pMouseX >= (x + imageWidth) + 30 && pMouseX <= (x + imageWidth) + 42) {
                    if (pMouseY >= y + ((imageHeight / 2) - 20) && pMouseY <= y + ((imageHeight / 2) + 20)) {
                        if (this.tabPage + 2 < getSortedTabs().size() / 6) {
                            tabPage += 1;
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.value(), 1.0F));
                            return true;
                        }
                    }
                }
                if (pMouseX >= x - 42 && pMouseX <= x - 30) {
                    if (pMouseY >= y + ((imageHeight / 2) - 20) && pMouseY <= y + ((imageHeight / 2) + 20)) {
                        if (tabPage > 0) {
                            tabPage -= 1;
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.value(), 1.0F));
                            return true;
                        }
                    }
                }
            }
        }
        if (pButton == 1 && screenType == 2) {
            screenType = 0;
            return true;
        }
        if (pButton == 0 && screenType == 2) {
            if (!clickedEntry.getPagesClient().isEmpty()) {
                int scrollbarX = x+247;
                int scrollbarY = y+10;
                scrollbarY += scrollbarPixels;
                boolean scrollbarHovered = pMouseX >= scrollbarX && pMouseY >= scrollbarY && pMouseX <= scrollbarX + 3 && pMouseY <= scrollbarY + 7;
                if (scrollbarHovered) {
                    scrolling = true;
                    return true;
                }
            }
            if (pMouseX >= (x+imageWidth)+6 && pMouseX <= (x+imageWidth)+18) {
                if (pMouseY >= y+((imageHeight/2)-20) && pMouseY <= y+((imageHeight/2)+20)) {
                    if (clickedEntry.getPagesClient().size() > page+1) {
                        scrollbarPixels = 0;
                        page += 1;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.value(), 1.0F));
                        return true;
                    }
                }
            }
            if (pMouseX >= x-18 && pMouseX <= x-6) {
                if (pMouseY >= y+((imageHeight/2)-20) && pMouseY <= y+((imageHeight/2)+20)) {
                    if (page > 0) {
                        scrollbarPixels = 0;
                        page -= 1;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.value(), 1.0F));
                        return true;
                    }
                }
            }
            if (pMouseX >= x+1 && pMouseX <= x+imageWidth-1) {
                if (pMouseY >= y + 1 && pMouseY <= y + imageHeight - 1) {
                    return clickedEntry.getPagesClient().get(page).onClick(this, pMouseX, pMouseY, pButton, x, y);
                }
            }
            if (pMouseX >= x+imageWidth && pMouseX <= x+imageWidth+17 && pMouseY >= y+10 && pMouseY <= y+10+21) {
                if (Screen.hasShiftDown()) {
                    savedEntry = null;
                    savedPage = 0;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.value(), 1.0F));
                }
                else {
                    savedEntry = clickedEntry.id;
                    savedPage = page;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.value(), 1.0F));
                    super.onClose();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            scrolling = false;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ticks++;
        if (canHaveBattery()) {
            if (SpecialConditionHandler.isAprilFools()) {
                battery -= ((1f / 20f) / 5f) * (Math.clamp(0, 1, (battery / 100f) * 3));///30f;
                if (battery < 1) {
                    battery = 1;
                }
            }
        }
    }
    public boolean canHaveBattery() {
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (scrolling) {
            if (!clickedEntry.getPagesClient().isEmpty()) {
                int scrollbarY = y + 10;
                int scrollbarArea = 139;
                scrollbarPixels = ((Math.clamp(0, scrollbarArea, mouseY-scrollbarY)));
            }
        }
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (screenType == 2) {
            if (!clickedEntry.getPagesClient().isEmpty()) {
                int scrollbarArea = 139;
                int scrollMax = Math.max(0, clickedEntry.getPagesClient().get(page).getMaxScrollY() - (imageHeight - 3));
                double scrollSpeed = 20/((float)scrollMax/50f);
                scrollbarPixels = ((Math.clamp(0, scrollbarArea, scrollbarPixels - (scrollY * scrollSpeed))));
            }
        }
        return false;
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Close on inventory key (E by default)
        if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            super.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
            if (!clickedEntry.getPagesClient().isEmpty()) {
                drawPage(clickedEntry.getPagesClient().get(page), graphics, delta, mouseX, mouseY);
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
                        if (isEntryUnlocked(entry)) {
                            Vector2i entryPosition = getEntryPosition(entry);
                            if (mouseX >= ((entryPosition.x * 20) - 10) + offsetX + x && mouseX <= ((entryPosition.x * 20) + 10) + offsetX + x) {
                                if (mouseY >= ((entryPosition.y * 20) - 10) + offsetY + y && mouseY <= ((entryPosition.y * 20) + 10) + offsetY + y) {
                                    Component name = entry.getName(getEntryCompletionStage(entry));
                                    Component flavor = entry.getFlavor(getEntryCompletionStage(entry));
                                    if (entry.isIncomplete(getEntryCompletionStage(entry))) {
                                        Component progressionRequirement = Component.translatable("tooltip.datanessence.progression_requirement").copy().withStyle(ChatFormatting.ITALIC).withColor(0xFFff61ca);
                                        tooltip = flavor.equals(Component.empty())
                                                ? List.of(name.getVisualOrderText(), progressionRequirement.getVisualOrderText())
                                                : List.of(name.getVisualOrderText(), flavor.copy().withStyle(ChatFormatting.ITALIC).withColor(EssenceTypeRegistry.ESSENCE.get().getColor()).getVisualOrderText(), progressionRequirement.getVisualOrderText());
                                    } else
                                        tooltip = flavor.equals(Component.empty())
                                            ? List.of(name.getVisualOrderText())
                                            : List.of(name.getVisualOrderText(), flavor.copy().withStyle(ChatFormatting.ITALIC).withColor(EssenceTypeRegistry.ESSENCE.get().getColor()).getVisualOrderText());
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
            graphics.drawCenteredString(Minecraft.getInstance().font, clickedEntry.getName(getEntryCompletionStage(clickedEntry)), x+imageWidth/2, y-(Minecraft.getInstance().font.lineHeight+4), 0xFFc90d8b);
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

    public double scrollbarPixels;
    public static float battery = 100;
    public void drawPage(Page page, GuiGraphics graphics, float pPartialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int scrollbarArea = 139;
        int scrollMax = Math.max(0, page.getMaxScrollY()-(imageHeight-3));
        int scrollShift = (int)(((float)scrollbarPixels/(float)scrollbarArea)*scrollMax);
        page.render(this, graphics, pPartialTick, mouseX, mouseY, x, y - scrollShift);
        graphics.disableScissor();
        if (scrollMax > 0) {
            graphics.blit(TEXTURE_PAGE, x + 246, y + 7, 25, 166, 5, 5);
            for (int i = 0; i < 142; i++) {
                graphics.blit(TEXTURE_PAGE, x + 246, y + 12 + i, 25, 171, 5, 1);
            }
            graphics.blit(TEXTURE_PAGE, x + 246, y + 154, 25, 184, 5, 5);
            int scrollbarX = x + 247;
            int scrollbarY = y + 10;
            scrollbarY += scrollbarPixels;
            boolean scrollbarHovered = mouseX >= scrollbarX && mouseY >= scrollbarY && mouseX <= scrollbarX + 3 && mouseY <= scrollbarY + 7;
            graphics.blit(TEXTURE_PAGE, scrollbarX, scrollbarY, 31, scrollbarHovered || scrolling ? 177 : 170, 3, 7);
        }
        if (this.page+1 < clickedEntry.getPagesClient().size()) {
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
        for (Entry destinationEntry : Entries.entries.values()) {
            if (destinationEntry.tab.equals(currentTab.id) && isEntryUnlocked(destinationEntry)) {
                for (Entry sourceEntry : getEntryParentEntries(destinationEntry)) {
                    if (sourceEntry.tab.equals(currentTab.id)) {
                        Vector2i entry1Pos = getEntryPosition(destinationEntry);
                        Vector2i entry2Pos = getEntryPosition(sourceEntry);
                        int sourceX = x + (entry1Pos.x * 20) + (int) offsetX;
                        int sourceY = y + (entry1Pos.y * 20) + (int) offsetY;
                        int destinationX = x + (entry2Pos.x * 20) + (int) offsetX;
                        int destinationY = y + (entry2Pos.y * 20) + (int) offsetY;

                        drawLine(sourceX, sourceY, destinationX, destinationY, sourceEntry, destinationEntry, pPartialTick, pMouseX, pMouseY);
                    }
                }
            }
        }

    }

    public void drawLine(int sourceX, int sourceY, int destinationX, int destinationY, Entry sourceEntry, Entry destinationEntry, float partialTick, int mouseX, int mouseY) {
        long time = System.currentTimeMillis() / 50;
        Vec3 origin = new Vec3(sourceX, sourceY, 0);
        Vec3 line = new Vec3(origin.x - destinationX, origin.y - destinationY, 0);
        int lineSegments = (int) Math.ceil(line.length() / 1);
        if (lineSegments > 0) {
            Tesselator tess = RenderSystem.renderThreadTesselator();
            RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
            RenderSystem.lineWidth(2f*(float)Minecraft.getInstance().getWindow().getGuiScale());
            GlStateManager._depthMask(false);
            GlStateManager._disableCull();
            int activeSegment = (int) (time % lineSegments);
            Vec3 segmentIteration = new Vec3(line.x / lineSegments, line.y / lineSegments, line.z / lineSegments);

            for (int i = lineSegments; i >= 0; i--) {
                double lx = origin.x();
                double ly = origin.y();
                origin = origin.subtract(segmentIteration);
                Vec2 normal = new Vec2((float) (origin.x - lx), (float) (origin.y - ly)).normalized();
                float blendSize = 8;
                float blend = Math.clamp(0f, 1f, (blendSize - Math.abs((float) i - (float) activeSegment)) / blendSize);
                int color = ColorUtil.blendColors(getLineColor1(sourceEntry, destinationEntry, partialTick, mouseX, mouseY), getLineColor2(sourceEntry, destinationEntry, partialTick, mouseX, mouseY), blend).getRGB();
                BufferBuilder builder = tess.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
                builder.addVertex((float) lx, (float) ly, 0).setColor(color).setNormal(normal.x, normal.y, 0);
                builder.addVertex((float) origin.x, (float) origin.y, 0).setColor(color).setNormal(normal.x, normal.y, 0);
                BufferUploader.drawWithShader(builder.buildOrThrow());
            }
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GlStateManager._enableCull();
        GlStateManager._depthMask(true);
        RenderSystem.lineWidth(1);
    }
    public Color getLineColor1(Entry source, Entry destination, float pPartialTick, int pMouseX, int pMouseY) {
        return new Color(17, 20, 38, 255);
    }
    public Color getLineColor2(Entry source, Entry destination, float pPartialTick, int pMouseX, int pMouseY) {
        return new Color(70, 216, 252, 255);
    }

    public List<DataTab> getSortedTabs() {
        return Entries.tabsSorted;
    }
    public List<DataTab> getCurrentTabs() {
        List<DataTab> tabs = new ArrayList<>();
        List<DataTab> sorted = getSortedTabs();
        List<DataTab> visible = new ArrayList<>();
        for (DataTab i : sorted.stream().toList()) {
            if (i.alwaysShown) {
                visible.add(i);
                continue;
            }
            for (Entry j : Entries.entries.values()) {
                if (isEntryUnlocked(j)) {
                    if (j.tab.equals(i.id)) {
                        visible.add(i);
                        break;
                    }
                }
            }
        }
        for (int i = tabPage*6; i < Math.min(visible.size(), (tabPage*6)+12); i++) {
            tabs.add(visible.get(i));
        }
        return tabs;
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    public void drawEntries(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        drawLines(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        for (Entry i : Entries.entries.values()) {
            if (i.tab.equals(currentTab.id)) {
                if (isEntryUnlocked(i)) {
                    Vector2i entryPosition = getEntryPosition(i);
                    int entryShift = 0;
                    int incompleteShift = 0;
                    if (pMouseX >= x && pMouseY >= y && pMouseX <= x+imageWidth && pMouseY <= y+imageHeight) {
                        if (pMouseX >= ((entryPosition.x * 20) - 10) + offsetX + x && pMouseX <= ((entryPosition.x * 20) + 10) + offsetX + x) {
                            if (pMouseY >= ((entryPosition.y * 20) - 10) + offsetY + y && pMouseY <= ((entryPosition.y * 20) + 10) + offsetY + y) {
                                entryShift = 40;
                                incompleteShift = 10;
                            }
                        }
                    }
                    pGuiGraphics.blit(TEXTURE_MAIN, x + ((entryPosition.x * 20) - 10) + (int) offsetX, y + ((entryPosition.y * 20) - 10) + (int) offsetY, 0, 166+entryShift, 20, 20);
                    pGuiGraphics.renderItem(i.getIcon(getEntryCompletionStage(i)), x + ((entryPosition.x * 20) - 8) + (int) offsetX, y + ((entryPosition.y * 20) - 8) + (int) offsetY);

                    if (i.isIncomplete(getEntryCompletionStage(i))) {
                        pGuiGraphics.blit(TEXTURE_MAIN, x + ((entryPosition.x * 20) + 5) + (int) offsetX, y + ((entryPosition.y * 20) + 5) + (int) offsetY, 0, 186+incompleteShift, 11, 10);
                    }
                }
            }
        }
    }
    public Vector2i getEntryPosition(Entry entry) {
        return new Vector2i(entry.x, entry.y);
    }
    public boolean isEntryUnlocked(Entry entry) {
        return entry.isDefault || entry.isVisibleClient();
    }
    public int getEntryCompletionStage(Entry entry) {
        return entry.getIncompleteStageClient();
    }
    public List<Entry> getEntryParentEntries(Entry entry) {
        return entry.getParentEntries();
    }
    public List<ResourceLocation> getEntryParents(Entry entry) {
        return entry.parents;
    }
}
