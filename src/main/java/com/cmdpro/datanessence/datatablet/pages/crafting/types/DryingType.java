package com.cmdpro.datanessence.datatablet.pages.crafting.types;

import com.cmdpro.datanessence.api.datatablet.CraftingType;
import com.cmdpro.datanessence.datatablet.pages.CraftingPage;
import com.cmdpro.datanessence.recipe.DryingRecipe;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class DryingType extends CraftingType {

    @Override
    public void render(CraftingPage page, DataTabletScreen screen, GuiGraphics pGuiGraphics, int xOffset, int x, int yOffset, int y, Recipe recipe, int pMouseX, int pMouseY) {
        if (recipe instanceof DryingRecipe dryingRecipe) {
            pGuiGraphics.blit(DataTabletScreen.TEXTURE_CRAFTING2, xOffset + x, yOffset + y, 133, 196, 123, 60);
            page.renderIngredientWithTooltip(screen, pGuiGraphics, Ingredient.of(BlockRegistry.DRYING_TABLE.get()), xOffset + x + 74, yOffset + y + 43, pMouseX, pMouseY);
            page.renderItemWithTooltip(pGuiGraphics, dryingRecipe.getResultItem(RegistryAccess.EMPTY), xOffset + x + 74, yOffset + y + 22, pMouseX, pMouseY);
            page.renderFluidWithTooltip(pGuiGraphics, dryingRecipe.getInput(), xOffset + x + 30, yOffset + y + 10, pMouseX, pMouseY);
            if (!dryingRecipe.getIngredients().isEmpty()) {
                page.renderIngredientWithTooltip(screen, pGuiGraphics, dryingRecipe.getIngredients().get(0), xOffset + x + 30, yOffset + y + 32, pMouseX, pMouseY);
            }
        }
    }

    @Override
    public boolean isRecipeType(Recipe recipe) {
            return recipe.getType().equals(RecipeRegistry.DRYING_TYPE.get());
    }
}
