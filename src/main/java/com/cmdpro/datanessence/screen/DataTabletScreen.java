package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.screen.datatablet.ClientEntries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.screen.datatablet.Page;
import com.cmdpro.datanessence.screen.datatablet.pages.TextPage;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSectionSerializer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import org.joml.Matrix4f;
import software.bernie.geckolib.util.RenderUtils;
import team.lodestar.lodestone.helpers.RenderHelper;
import team.lodestar.lodestone.helpers.VecHelper;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.nio.charset.MalformedInputException;

public class DataTabletScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/datatablet.png");
    public DataTabletScreen(Component pTitle) {
        super(pTitle);
        offsetX = (imageWidth/2);
        offsetY = (imageHeight/2);
    }
    public static int imageWidth = 256;
    public static int imageHeight = 166;
    public double offsetX;
    public double offsetY;
    public int screenType;
    public Entry clickedEntry;
    public int page;
    public int ticks;

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton == 0 && (screenType == 0 || screenType == 1)) {
            offsetX += pDragX;
            offsetY += pDragY;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (pButton == 0 && screenType == 0) {
            for (Entry i : ClientEntries.entries.values()) {
                if (pMouseX >= ((i.x*20)-10)+offsetX+x && pMouseX <= ((i.x*20)+10)+offsetX+x) {
                    if (pMouseY >= ((i.y*20)-10)+offsetY+y && pMouseY <= ((i.y*20)+10)+offsetY+y) {
                        screenType = 2;
                        clickedEntry = i;
                        page = 0;
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

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        renderBg(graphics, delta, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.enableScissor(x+1, y+1, x+imageWidth-1, y+imageHeight-1);
        if (screenType == 0) {
            drawEntries(graphics, delta, mouseX, mouseY);
        } else if (screenType == 1) {

        } else if (screenType == 2) {
            if (clickedEntry.pages.length > 0) {
                drawPage(clickedEntry.pages[page], graphics, delta, mouseX, mouseY);
            }
        }
        graphics.disableScissor();
        super.render(graphics, mouseX, mouseY, delta);
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
        for (Entry i : ClientEntries.entries.values()) {
            if (i.getParentEntry() != null) {
                int x1 = x+((i.x*20)+1)+(int)offsetX;
                int y1 = y+((i.y*20)+1)+(int)offsetY;
                int x2 = x+((i.getParentEntry().x*20)+1)+(int)offsetX;
                int y2 = y+((i.getParentEntry().y*20)+1)+(int)offsetY;
                buf.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
                buf.vertex(x1, y1, 0.0D).color(83, 0, 81, 255).normal(x2-x1, y2-y1, 0).endVertex();
                buf.vertex(x2, y2, 0.0D).color(83, 0, 81, 255).normal(x1-x2, y1-y2, 0).endVertex();
                tess.end();
            }
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GlStateManager._enableCull();
        GlStateManager._depthMask(true);
        RenderSystem.lineWidth(1);
    }
    public void drawEntries(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        drawLines(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        for (Entry i : ClientEntries.entries.values()) {
            pGuiGraphics.blit(TEXTURE, x+((i.x*20)-10)+(int)offsetX, y+((i.y*20)-10)+(int)offsetY, 0, 166, 20, 20);
            pGuiGraphics.renderItem(i.icon, x+((i.x*20)-8)+(int)offsetX, y+((i.y*20)-8)+(int)offsetY);
        }
    }
}
