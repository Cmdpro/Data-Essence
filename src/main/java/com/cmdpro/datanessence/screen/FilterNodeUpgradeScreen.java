package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class FilterNodeUpgradeScreen extends AbstractContainerScreen<FilterNodeUpgradeMenu> {
    public static final ResourceLocation TEXTURE = DataNEssence.locate("textures/gui/filter_node_upgrade.png");
    public FilterNodeUpgradeScreen(FilterNodeUpgradeMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        ItemContainerContents contents = menu.stack.get(DataComponentRegistry.FILTER_STACK);
        if (contents != null && contents.getSlots() >= 1) {
            ItemStack filterStack = contents.getStackInSlot(0);
            pGuiGraphics.renderItem(filterStack, x+80, y+34);
        }
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
