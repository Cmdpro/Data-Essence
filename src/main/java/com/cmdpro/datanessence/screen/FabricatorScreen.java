package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceBarBackgroundTypes;
import com.cmdpro.datanessence.api.util.client.ClientEssenceBarUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
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

public class FabricatorScreen extends AbstractContainerScreen<FabricatorMenu> {
    public static final ResourceLocation TEXTURE = DataNEssence.locate("textures/gui/fabricator.png");
    public FabricatorScreen(FabricatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    public float time;
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.25f);
        pGuiGraphics.renderItem(menu.blockEntity.item, x+145, y+35);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
        ClientEssenceBarUtil.drawEssenceBar(pGuiGraphics, x+8, y+17, EssenceTypeRegistry.ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()), menu.blockEntity.getStorage().getMaxEssence(), EssenceBarBackgroundTypes.INDUSTRIAL);
        ClientEssenceBarUtil.drawEssenceIcon(pGuiGraphics, x+7, y+6, EssenceTypeRegistry.ESSENCE.get(), EssenceBarBackgroundTypes.INDUSTRIAL, ClientPlayerData.getUnlockedEssences().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()), false));
        pGuiGraphics.blit(TEXTURE, x+110, y+34, 177, 0, (int)(22f*((float)menu.blockEntity.time/menu.blockEntity.maxTime)), 17);
        time += pPartialTick;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        List<FormattedCharSequence> component = new ArrayList<>();
        Component essence = ClientEssenceBarUtil.getEssenceBarTooltip(pMouseX, pMouseY, x+8, y+17, EssenceTypeRegistry.ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()));
        if (essence != null) {
            component.clear();
            component.add(essence.getVisualOrderText());
        }
        Component lunarEssence = ClientEssenceBarUtil.getEssenceBarTooltip(pMouseX, pMouseY, x+19, y+17, EssenceTypeRegistry.LUNAR_ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.LUNAR_ESSENCE.get()));
        if (lunarEssence != null) {
            component.clear();
            component.add(lunarEssence.getVisualOrderText());
        }
        Component naturalEssence = ClientEssenceBarUtil.getEssenceBarTooltip(pMouseX, pMouseY, x+30, y+17, EssenceTypeRegistry.NATURAL_ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.NATURAL_ESSENCE.get()));
        if (naturalEssence != null) {
            component.clear();
            component.add(naturalEssence.getVisualOrderText());
        }
        Component exoticEssence = ClientEssenceBarUtil.getEssenceBarTooltip(pMouseX, pMouseY, x+41, y+17, EssenceTypeRegistry.EXOTIC_ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.EXOTIC_ESSENCE.get()));
        if (exoticEssence != null) {
            component.clear();
            component.add(exoticEssence.getVisualOrderText());
        }
        if (component != null) {
            pGuiGraphics.renderTooltip(font, component, pMouseX, pMouseY);
        }
    }
}
