package com.cmdpro.datanessence.integration.jei;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.ItemRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.InfusionRecipe;
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
    public static final RecipeType FABRICATION_RECIPE = RecipeType.create(DataNEssence.MOD_ID, RecipeRegistry.FABRICATIONCRAFTING.getId().getPath(), IFabricationRecipe.class);
    public static final RecipeType INFUSION_RECIPE = RecipeType.create(DataNEssence.MOD_ID, RecipeRegistry.INFUSION.getId().getPath(), InfusionRecipe.class);
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FabricatorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new InfuserRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<IFabricationRecipe> recipes = rm.getAllRecipesFor(RecipeRegistry.FABRICATIONCRAFTING.get());
        registration.addRecipes(FABRICATION_RECIPE, recipes);
        List<InfusionRecipe> recipes2 = rm.getAllRecipesFor(InfusionRecipe.Type.INSTANCE);
        registration.addRecipes(INFUSION_RECIPE, recipes2);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ItemRegistry.FABRICATOR_ITEM.get()), FABRICATION_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(ItemRegistry.INFUSER_ITEM.get()), INFUSION_RECIPE);
    }
    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

        runTime = jeiRuntime;

    }
}
