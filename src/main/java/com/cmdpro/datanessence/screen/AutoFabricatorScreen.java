package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.api.util.client.ClientEssenceBarUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.screen.widget.LockWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class AutoFabricatorScreen extends AbstractContainerScreen<AutoFabricatorMenu> {
    public static final ResourceLocation TEXTURE = DataNEssence.locate("textures/gui/auto-fabricator.png");
    public AutoFabricatorScreen(AutoFabricatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        addRenderableWidget(new LockWidget(menu.blockEntity, (LockableItemHandler)menu.blockEntity.getItemHandler(), x+128, y+3));
    }

    public float time;
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        ClientRenderingUtil.renderLockedSlotBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY, x, y, menu.slots);
        pGuiGraphics.blit(TEXTURE, x+111, y+34, 221, 0, (int)(22f*((float)menu.blockEntity.craftingProgress/menu.blockEntity.maxTime)), 17);
        ClientEssenceBarUtil.drawEssenceBar(pGuiGraphics, x+8, y+17, EssenceTypeRegistry.ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()), menu.blockEntity.getStorage().getMaxEssence());
        ClientEssenceBarUtil.drawEssenceBar(pGuiGraphics, x+19, y+17, EssenceTypeRegistry.LUNAR_ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.LUNAR_ESSENCE.get()), menu.blockEntity.getStorage().getMaxEssence());
        ClientEssenceBarUtil.drawEssenceBar(pGuiGraphics, x+30, y+17, EssenceTypeRegistry.NATURAL_ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.NATURAL_ESSENCE.get()), menu.blockEntity.getStorage().getMaxEssence());
        ClientEssenceBarUtil.drawEssenceBar(pGuiGraphics, x+41, y+17, EssenceTypeRegistry.EXOTIC_ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.EXOTIC_ESSENCE.get()), menu.blockEntity.getStorage().getMaxEssence());
        if (ClientPlayerData.getUnlockedEssences().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()), false)) {
            pGuiGraphics.blit(TEXTURE, x+7, y+6, 177, 0, 9, 9);
        }
        if (ClientPlayerData.getUnlockedEssences().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.LUNAR_ESSENCE.get()), false)) {
            pGuiGraphics.blit(TEXTURE, x+18, y+6, 188, 0, 9, 9);
        }
        if (ClientPlayerData.getUnlockedEssences().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.NATURAL_ESSENCE.get()), false)) {
            pGuiGraphics.blit(TEXTURE, x+29, y+6, 199, 0, 9, 9);
        }
        if (ClientPlayerData.getUnlockedEssences().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.EXOTIC_ESSENCE.get()), false)) {
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
        Component essence = ClientEssenceBarUtil.getEssenceBarTooltip(pMouseX, pMouseY, x+8, y+17, EssenceTypeRegistry.ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()));
        if (essence != null) {
            component.clear();
            component.add(essence.getVisualOrderText());
        }
        if (component != null) {
            pGuiGraphics.renderTooltip(font, component, pMouseX, pMouseY);
        }
    }
}
