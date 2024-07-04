package com.cmdpro.datanessence.integration.emi;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.InfusionRecipe;
import com.cmdpro.datanessence.registry.ItemRegistry;

import com.cmdpro.datanessence.registry.RecipeRegistry;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@EmiEntrypoint
public class EMIDataNEssencePlugin implements EmiPlugin {
    public static final ResourceLocation EMI_ICONS = new ResourceLocation(DataNEssence.MOD_ID,"textures/gui/emi/emi_icons.png");

    public static final EmiStack FABRICATOR_WORKSTATION = EmiStack.of(ItemRegistry.FABRICATOR_ITEM.get());
    public static final EmiStack INFUSER_WORKSTATION = EmiStack.of(ItemRegistry.INFUSER_ITEM.get());

    public static final EmiRecipeCategory FABRICATION = new EmiRecipeCategory(new ResourceLocation(DataNEssence.MOD_ID,"fabrication"), FABRICATOR_WORKSTATION, new EmiTexture(EMI_ICONS, 0, 0, 16, 16));
    public static final EmiRecipeCategory INFUSION = new EmiRecipeCategory(new ResourceLocation(DataNEssence.MOD_ID, "infusion"), INFUSER_WORKSTATION, new EmiTexture(EMI_ICONS, 16, 0, 16, 16));

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addCategory(FABRICATION);
        emiRegistry.addCategory(INFUSION);

        emiRegistry.addWorkstation(FABRICATION, FABRICATOR_WORKSTATION);
        emiRegistry.addWorkstation(INFUSION, INFUSER_WORKSTATION);

        RecipeManager manager = emiRegistry.getRecipeManager();

        for (IFabricationRecipe recipe : manager.getAllRecipesFor(RecipeRegistry.FABRICATIONCRAFTING.get())) {
            emiRegistry.addRecipe(new EMIFabricationRecipe(recipe));
        }
        for (InfusionRecipe recipe : manager.getAllRecipesFor(InfusionRecipe.Type.INSTANCE)) {
            emiRegistry.addRecipe(new EMIInfusionRecipe(recipe));
        }
    }
}
