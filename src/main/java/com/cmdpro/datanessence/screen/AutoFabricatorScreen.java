package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.LockableItemHandler;
import com.cmdpro.datanessence.screen.widget.LockWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class AutoFabricatorScreen extends AbstractContainerScreen<AutoFabricatorMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/auto-fabricator.png");
    public AutoFabricatorScreen(AutoFabricatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        addRenderableWidget(new LockWidget(menu.blockEntity, (LockableItemHandler)menu.blockEntity.getItemHandler(), x+113, y+52));
    }

    public float time;
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        ClientDataNEssenceUtil.renderLockedSlotBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY, x, y, menu.slots);
        pGuiGraphics.blit(TEXTURE, x+111, y+34, 221, 0, (int)(22f*((float)menu.blockEntity.craftingProgress/50f)), 17);
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBar(pGuiGraphics, x+8, y+17, 0, menu.blockEntity.getEssence(), menu.blockEntity.getMaxEssence());
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBar(pGuiGraphics, x+19, y+17, 1, menu.blockEntity.getLunarEssence(), menu.blockEntity.getMaxLunarEssence());
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBar(pGuiGraphics, x+30, y+17, 2, menu.blockEntity.getNaturalEssence(), menu.blockEntity.getMaxNaturalEssence());
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBar(pGuiGraphics, x+41, y+17, 3, menu.blockEntity.getExoticEssence(), menu.blockEntity.getMaxExoticEssence());
        if (ClientPlayerData.getUnlockedEssences()[0]) {
            pGuiGraphics.blit(TEXTURE, x+7, y+6, 177, 0, 9, 9);
        }
        if (ClientPlayerData.getUnlockedEssences()[1]) {
            pGuiGraphics.blit(TEXTURE, x+18, y+6, 188, 0, 9, 9);
        }
        if (ClientPlayerData.getUnlockedEssences()[2]) {
            pGuiGraphics.blit(TEXTURE, x+29, y+6, 199, 0, 9, 9);
        }
        if (ClientPlayerData.getUnlockedEssences()[3]) {
            pGuiGraphics.blit(TEXTURE, x+40, y+6, 210, 0, 9, 9);
        }
        time += pPartialTick;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        List<FormattedCharSequence> component = new ArrayList<>();
        Component essence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltip(pMouseX, pMouseY, x+8, y+17, 0, menu.blockEntity.getEssence());
        if (essence != null) {
            component.clear();
            component.add(essence.getVisualOrderText());
        }
        Component lunarEssence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltip(pMouseX, pMouseY, x+19, y+17, 1, menu.blockEntity.getLunarEssence());
        if (lunarEssence != null) {
            component.clear();
            component.add(lunarEssence.getVisualOrderText());
        }
        Component naturalEssence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltip(pMouseX, pMouseY, x+30, y+17, 2, menu.blockEntity.getNaturalEssence());
        if (naturalEssence != null) {
            component.clear();
            component.add(naturalEssence.getVisualOrderText());
        }
        Component exoticEssence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltip(pMouseX, pMouseY, x+41, y+17, 3, menu.blockEntity.getExoticEssence());
        if (exoticEssence != null) {
            component.clear();
            component.add(exoticEssence.getVisualOrderText());
        }
        if (component != null) {
            pGuiGraphics.renderTooltip(font, component, pMouseX, pMouseY);
        }
    }
}
