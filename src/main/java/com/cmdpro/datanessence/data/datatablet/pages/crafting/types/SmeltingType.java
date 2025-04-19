package com.cmdpro.datanessence.data.datatablet.pages.crafting.types;

import com.cmdpro.datanessence.api.datatablet.CraftingType;
import com.cmdpro.datanessence.data.datatablet.pages.CraftingPage;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class SmeltingType extends CraftingType {
    @Override
    public void render(CraftingPage page, DataTabletScreen screen, GuiGraphics graphics, int xOffset, int x, int yOffset, int y, Recipe recipe, int pMouseX, int pMouseY) {
        if (recipe instanceof SmeltingRecipe smeltingRecipe) {
            graphics.blit(DataTabletScreen.TEXTURE_CRAFTING, xOffset + x, yOffset + y, 10, 16, 123, 60);
            page.renderIngredientWithTooltip(screen, graphics, Ingredient.of(Items.FURNACE), xOffset + x + 74, yOffset + y + 43, pMouseX, pMouseY);
            page.renderItemWithTooltip(graphics, recipe.getResultItem(RegistryAccess.EMPTY), xOffset + x + 74, yOffset + y + 22, pMouseX, pMouseY);
            page.renderIngredientWithTooltip(screen, graphics, smeltingRecipe.getIngredients().get(0), xOffset + x + 30, yOffset + y + 22, pMouseX, pMouseY);
        }
    }

    @Override
    public boolean isRecipeType(Recipe recipe) {
        return recipe.getType().equals(RecipeType.SMELTING);
    }
}
