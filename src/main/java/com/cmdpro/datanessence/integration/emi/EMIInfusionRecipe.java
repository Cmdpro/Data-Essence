package com.cmdpro.datanessence.integration.emi;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.recipe.InfusionRecipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class EMIInfusionRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public EMIInfusionRecipe(InfusionRecipe recipe) {
        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getIngredients().stream().map(s -> EmiIngredient.of(Arrays.stream(s.getItems()).map(EmiStack::of).toList())).toList()));
        this.output = List.of(EmiStack.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess())));
    }
    @Override
    public EmiRecipeCategory getCategory() {
        return EMIDataNEssencePlugin.INFUSION;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
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
    public void addWidgets(WidgetHolder widgetHolder) {
        ResourceLocation background = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting.png");

        widgetHolder.addTexture(background, 0, 0, 123, 60, 136, 10);

        // Input
        widgetHolder.addSlot(input.get(0), 0, 0);
        // Output
        widgetHolder.addSlot(output.get(0), 50, 0).recipeContext(this);
    }
}
