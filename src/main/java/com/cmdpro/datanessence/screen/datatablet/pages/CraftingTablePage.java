package com.cmdpro.datanessence.screen.datatablet.pages;

import com.cmdpro.datanessence.item.DataTablet;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.cmdpro.datanessence.screen.datatablet.Page;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.serializers.CraftingTablePageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.serializers.TextPageSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CraftingTablePage extends TextPage {
    public CraftingTablePage(Component text, ResourceLocation[] recipes) {
        super(text);
        this.recipes = recipes;
    }
    public ResourceLocation[] recipes;
    @Override
    public int textYOffset() {
        int x = 3;
        int y = 3;
        for (ResourceLocation i : recipes) {
            x += 127;
            if (x > 254) {
                x = 3;
                y += 64;
            }
        }
        return y+58;
    }
    public List<FormattedCharSequence> tooltipToShow = new ArrayList<>();
    public boolean showTooltip;
    @Override
    public void render(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {
        super.render(screen, pGuiGraphics, pPartialTick, pMouseX, pMouseY, xOffset, yOffset);
        int x = 3;
        int y = 3;
        for (ResourceLocation i : recipes) {
            Optional<? extends Recipe<?>> optional = Minecraft.getInstance().level.getRecipeManager().byKey(i);
            if (optional.isPresent()) {
                if (optional.get() instanceof CraftingRecipe recipe) {
                    pGuiGraphics.blit(DataTabletScreen.TEXTURE, xOffset + x, yOffset + y, 133, 196, 123, 60);
                    renderItemWithTooltip(pGuiGraphics, recipe.getResultItem(RegistryAccess.EMPTY), xOffset + x + 92, yOffset + y + 22, pMouseX, pMouseY);
                    if (recipe instanceof ShapelessRecipe) {
                        pGuiGraphics.blit(DataTabletScreen.TEXTURE, xOffset + x + 92, yOffset + y + 7, 240, 185, 16, 11);
                    }
                    int x2 = 1;
                    int y2 = 1;
                    int p = 0;
                    int wrap = 3;
                    if (recipe instanceof ShapedRecipe shaped) {
                        wrap = shaped.getWidth();
                    }
                    for (Ingredient o : recipe.getIngredients()) {
                        renderIngredientWithTooltip(screen, pGuiGraphics, o, xOffset + x + 14 + x2, yOffset + y + 4 + y2, pMouseX, pMouseY);
                        x2 += 17;
                        p++;
                        if (p >= wrap) {
                            p = 0;
                            x2 = 1;
                            y2 += 17;
                        }
                    }
                }
            }
            x += 127;
            if (x > 254) {
                x = 3;
                y += 64;
            }
        }
    }

    @Override
    public void renderPost(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {
        super.renderPost(screen, pGuiGraphics, pPartialTick, pMouseX, pMouseY, xOffset, yOffset);
        if (showTooltip) {
            showTooltip = false;
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipToShow, pMouseX, pMouseY);
        }
    }
    public void renderItemWithTooltip(GuiGraphics graphics, ItemStack item, int x, int y, int mouseX, int mouseY) {
        graphics.renderItem(item, x, y);
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
    public void renderIngredientWithTooltip(DataTabletScreen screen, GuiGraphics graphics, Ingredient item, int x, int y, int mouseX, int mouseY) {
        if (!item.isEmpty()) {
            ItemStack currentItem = item.getItems()[(screen.ticks / 20) % item.getItems().length];
            graphics.renderItem(currentItem, x, y);
            if (mouseX >= x && mouseY >= y) {
                if (mouseX <= x + 16 && mouseY <= y + 16) {
                    showTooltip = true;
                    tooltipToShow.clear();
                    for (Component i : Screen.getTooltipFromItem(Minecraft.getInstance(), currentItem)) {
                        tooltipToShow.add(i.getVisualOrderText());
                    }
                }
            }
        }
    }
    @Override
    public PageSerializer getSerializer() {
        return CraftingTablePageSerializer.INSTANCE;
    }
}
