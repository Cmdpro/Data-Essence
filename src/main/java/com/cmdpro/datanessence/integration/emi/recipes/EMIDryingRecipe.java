package com.cmdpro.datanessence.integration.emi.recipes;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.integration.emi.DataNEssenceEMIPlugin;
import com.cmdpro.datanessence.integration.emi.widgets.EssenceBarWidget;
import com.cmdpro.datanessence.recipe.DryingRecipe;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EMIDryingRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final EmiStack input;
    private final EmiIngredient additive;
    private final List<EmiStack> output;

    public EMIDryingRecipe(ResourceLocation id, DryingRecipe recipe) {
        this.id = id;
        this.input = EmiStack.of(recipe.getInput().getFluid(), recipe.getInput().getAmount());
        this.additive = recipe.getIngredients().isEmpty() ? null : EmiIngredient.of(recipe.getIngredients().get(0));
        this.output = List.of(EmiStack.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess())));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return DataNEssenceEMIPlugin.DRYING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        ArrayList<EmiIngredient> ret = new ArrayList<>();
        ret.add(input);
        if (additive != null)
            ret.add(additive);
        return ret;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 123;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation background = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting2.png");

        widgets.addTexture(background, 0, 0, getDisplayWidth(), getDisplayHeight(), 133, 196);

        // Input
        widgets.addSlot(input, 29, 9).drawBack(false);
        if (additive != null) {
            widgets.addSlot(additive, 29, 31).drawBack(false);
        }
        // Output
        widgets.addSlot(output.get(0), 73, 21).recipeContext(this).drawBack(false);
    }


}
