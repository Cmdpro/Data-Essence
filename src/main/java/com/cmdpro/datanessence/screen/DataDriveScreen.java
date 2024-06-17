package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.PlayerChangeDriveDataC2SPacket;
import com.cmdpro.datanessence.networking.packet.PlayerFinishDataBankMinigameC2SPacket;
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

public class DataDriveScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/data_tablet.png");
    public DataDriveScreen(Component pTitle, boolean offhand) {
        super(pTitle);
        offsetX = (imageWidth/2);
        offsetY = (imageHeight/2);
        this.offhand = offhand;
    }
    public boolean offhand;
    public static int imageWidth = 256;
    public static int imageHeight = 166;
    public double offsetX;
    public double offsetY;

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton == 0) {
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
        if (pButton == 0) {
            for (Entry i : Entries.entries.values()) {
                if (ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                    if (pMouseX >= ((i.x * 20) - 10) + offsetX + x && pMouseX <= ((i.x * 20) + 10) + offsetX + x) {
                        if (pMouseY >= ((i.y * 20) - 10) + offsetY + y && pMouseY <= ((i.y * 20) + 10) + offsetY + y) {
                            ModMessages.sendToServer(new PlayerChangeDriveDataC2SPacket(i.id, offhand));
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            onClose();
                            return true;
                        }
                    }
                }
            }
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        renderBg(graphics, delta, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.enableScissor(x+3, y+3, x+imageWidth-3, y+imageHeight-3);
        drawEntries(graphics, delta, mouseX, mouseY);
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 399);
        graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("item.datanessence.data_drive.text", ClientPlayerData.getTier()), x+(imageWidth/2), y+8, 0xFFc90d8b);
        graphics.blit(TEXTURE, x+3, y+3, 48, 166, 4, 4);
        graphics.blit(TEXTURE, x+3, y+imageHeight-7, 48, 170, 4, 4);
        graphics.blit(TEXTURE, x+imageWidth-7, y+3, 52, 166, 4, 4);
        graphics.blit(TEXTURE, x+imageWidth-7, y+imageHeight-7, 52, 170, 4, 4);
        graphics.pose().popPose();
        graphics.disableScissor();
        super.render(graphics, mouseX, mouseY, delta);
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
            if (ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                if (i.getParentEntry() != null) {
                    int x1 = x + ((i.x * 20) + 1) + (int) offsetX;
                    int y1 = y + ((i.y * 20) + 1) + (int) offsetY;
                    int x2 = x + ((i.getParentEntry().x * 20) + 1) + (int) offsetX;
                    int y2 = y + ((i.getParentEntry().y * 20) + 1) + (int) offsetY;
                    buf.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
                    buf.vertex(x1, y1, 0.0D).color(201, 13, 139, 255).normal(x2 - x1, y2 - y1, 0).endVertex();
                    buf.vertex(x2, y2, 0.0D).color(201, 13, 139, 255).normal(x1 - x2, y1 - y2, 0).endVertex();
                    tess.end();
                }
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
        for (Entry i : Entries.entries.values()) {
            if (ClientPlayerUnlockedEntries.getUnlocked().contains(i.id)) {
                pGuiGraphics.blit(TEXTURE, x + ((i.x * 20) - 10) + (int) offsetX, y + ((i.y * 20) - 10) + (int) offsetY, 0, 166, 20, 20);
                pGuiGraphics.renderItem(i.icon, x + ((i.x * 20) - 8) + (int) offsetX, y + ((i.y * 20) - 8) + (int) offsetY);
            }
        }
    }
}
