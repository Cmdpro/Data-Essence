package com.cmdpro.datanessence.integration.jei;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.init.ItemInit;
import com.cmdpro.datanessence.init.RecipeInit;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.*;

@JeiPlugin
public class JEIDataNEssencePlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(DataNEssence.MOD_ID, "jei_plugin");
    }

    public static IJeiRuntime runTime;
    public static final RecipeType FABRICATIONRECIPE = RecipeType.create(DataNEssence.MOD_ID, RecipeInit.FABRICATIONCRAFTING.getId().getPath(), IFabricationRecipe.class);
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FabricatorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<IFabricationRecipe> recipes = rm.getAllRecipesFor(RecipeInit.FABRICATIONCRAFTING.get());
        registration.addRecipes(FABRICATIONRECIPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ItemInit.FABRICATOR_ITEM.get()), FABRICATIONRECIPE);
    }
    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

        runTime = jeiRuntime;

    }
}
