package com.cmdpro.datanessence.data.datatablet.pages;

import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.api.datatablet.CraftingType;
import com.cmdpro.datanessence.api.datatablet.CraftingTypes;
import com.cmdpro.datanessence.data.datatablet.pages.serializers.CraftingPageSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CraftingPage extends TextPage {
    public CraftingPage(Component text, boolean rtl, List<ResourceLocation> recipes) {
        super(text, rtl);
        this.recipes = recipes;
    }
    public List<ResourceLocation> recipes;
    @Override
    public int textYOffset() {
        int x = 4;
        int y = 4;
        int o = 0;
        for (ResourceLocation i : recipes) {
            x += 125;
            if (o >= 2) {
                x = 4;
                y += 62;
                o = 0;
            }
            o++;
        }
        return y+58;
    }
    public List<FormattedCharSequence> tooltipToShow = new ArrayList<>();
    public boolean showTooltip;
    @Override
    public void render(DataTabletScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {
        super.render(screen, pGuiGraphics, pPartialTick, pMouseX, pMouseY, xOffset, yOffset);
        int x = 4;
        int y = 4;
        int p = 0;
        for (ResourceLocation i : recipes) {
            Optional<? extends RecipeHolder<?>> optional = Minecraft.getInstance().level.getRecipeManager().byKey(i);
            if (optional.isPresent()) {
                for (CraftingType o : CraftingTypes.types) {
                    if (o.isRecipeType(optional.get().value())) {
                        o.render(this, screen, pGuiGraphics, xOffset, x, yOffset, y, optional.get().value(), pMouseX, pMouseY);
                        break;
                    }
                }
            }
            x += 125;
            p++;
            if (p >= 2) {
                x = 4;
                y += 62;
                p = 0;
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
    public void renderFluidIngredientWithTooltip(DataTabletScreen screen, GuiGraphics graphics, FluidIngredient fluid, int x, int y, int mouseX, int mouseY) {
        if (!fluid.isEmpty()) {
            FluidStack currentFluid = fluid.getStacks()[(screen.ticks / 20) % fluid.getStacks().length];
            renderFluidWithTooltip(graphics, currentFluid, x, y, mouseX, mouseY);
        }
    }
    public void renderFluidWithTooltip(GuiGraphics graphics, FluidStack fluid, int x, int y, int mouseX, int mouseY) {
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluid);
        if (stillTexture != null) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
            int tintColor = fluidTypeExtensions.getTintColor(fluid);
            Color tint = new Color(tintColor);
            graphics.blit(x, y, 0, 16, 16, sprite, (float)tint.getRed()/255f, (float)tint.getGreen()/255f, (float)tint.getBlue()/255f, (float)tint.getAlpha()/255f);
        }
        if (mouseX <= x+16 && mouseY <= y+16 && mouseX >= x && mouseY >= y) {
            if (!fluid.isEmpty()) {
                showTooltip = true;
                tooltipToShow.clear();
                tooltipToShow.add(Component.translatable("gui.widget.fluid.without_max", fluid.getAmount(), fluid.getHoverName()).getVisualOrderText());
            } else {
                showTooltip = true;
                tooltipToShow.clear();
                tooltipToShow.add(Component.empty().getVisualOrderText());
            }
        }
    }
    @Override
    public PageSerializer getSerializer() {
        return CraftingPageSerializer.INSTANCE;
    }
}
