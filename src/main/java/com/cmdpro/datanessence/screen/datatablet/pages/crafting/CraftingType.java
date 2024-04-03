package com.cmdpro.datanessence.screen.datatablet.pages.crafting;

import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.cmdpro.datanessence.screen.datatablet.pages.CraftingPage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class CraftingType {
    public abstract void render(CraftingPage page, DataTabletScreen screen, GuiGraphics pGuiGraphics, int xOffset, int x, int yOffset, int y, Recipe recipe, int pMouseX, int pMouseY);
    public abstract boolean isRecipeType(Recipe recipe);
}
