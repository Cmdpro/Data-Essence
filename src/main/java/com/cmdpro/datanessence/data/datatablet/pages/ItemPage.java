package com.cmdpro.datanessence.data.datatablet.pages;

import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.data.datatablet.pages.serializers.ItemPageSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemPage extends TextPage {
    public ItemStack item;
    public ItemPage(Component text, boolean rtl, ItemStack item) {
        super(text, rtl);
        this.item = item;
    }

    public List<FormattedCharSequence> tooltipToShow = new ArrayList<>();
    public boolean showTooltip;
    @Override
    public void render(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {
        super.render(screen, pGuiGraphics, pPartialTick, pMouseX, pMouseY, xOffset, yOffset);
        pGuiGraphics.blit(DataTabletScreen.TEXTURE_MISC, xOffset + ((DataTabletScreen.imageWidth/2)-19), yOffset + 4, 0, 0, 38, 38);
        renderItemWithTooltip(pGuiGraphics, item, xOffset + ((DataTabletScreen.imageWidth/2)-19)+11, yOffset + 15, pMouseX, pMouseY);
    }
    @Override
    public void renderPost(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {
        super.renderPost(screen, pGuiGraphics, pPartialTick, pMouseX, pMouseY, xOffset, yOffset);
        if (showTooltip) {
            showTooltip = false;
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipToShow, pMouseX, pMouseY);
        }
    }

    @Override
    public int textYOffset() {
        return 44;
    }

    public void renderItemWithTooltip(GuiGraphics graphics, ItemStack item, int x, int y, int mouseX, int mouseY) {
        graphics.renderItem(item, x, y);
        graphics.renderItemDecorations(Minecraft.getInstance().font, item, x, y);
        if (mouseX >= x && mouseY >= y) {
            if (mouseX <= x + 16 && mouseY <= y + 16) {
                showTooltip = true;
                tooltipToShow.clear();
                for (Component i : Screen.getTooltipFromItem(Minecraft.getInstance(), item)) {
                    tooltipToShow.add(i.getVisualOrderText());
                }
            }
        }
    }
    @Override
    public PageSerializer getSerializer() {
        return ItemPageSerializer.INSTANCE;
    }
}
