package com.cmdpro.datanessence.integration.emi.recipes;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.integration.emi.EMIDataNEssencePlugin;
import com.cmdpro.datanessence.integration.emi.widgets.EssenceBarWidget;
import com.cmdpro.datanessence.recipe.FluidMixingRecipe;
import com.cmdpro.datanessence.recipe.InfusionRecipe;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EMIFluidMixingRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final float essenceCost;

    public EMIFluidMixingRecipe(ResourceLocation id, FluidMixingRecipe recipe) {
        this.id = id;
        this.input = List.of(
                EmiIngredient.of(Arrays.stream(recipe.getInput1().getStacks()).map(NeoForgeEmiStack::of).toList()),
                EmiIngredient.of(Arrays.stream(recipe.getInput2().getStacks()).map(NeoForgeEmiStack::of).toList()),
                EmiIngredient.of(recipe.getIngredients().get(0))
        );
        this.output = List.of(EmiStack.of(recipe.getOutput().getFluid(), recipe.getOutput().getAmount()));
        this.essenceCost = recipe.getEssenceCost();
    }
    @Override
    public EmiRecipeCategory getCategory() {
        return EMIDataNEssencePlugin.FLUID_MIXING;
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
        ResourceLocation background = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting.png");

        widgetHolder.addTexture(background, 0, 0, getDisplayWidth(), getDisplayHeight(), 10, 76);

        // Input
        widgetHolder.addSlot(input.get(0), 23, 11).drawBack(false);
        widgetHolder.addSlot(input.get(1), 23, 33).drawBack(false);
        widgetHolder.addSlot(input.get(2), 34, 21).drawBack(false);
        // Output
        widgetHolder.addSlot(output.get(0), 77, 21).recipeContext(this).drawBack(false);

        // Essence bars
        widgetHolder.add(new EssenceBarWidget(5, 19, EssenceTypeRegistry.ESSENCE.get(), essenceCost));
    }
}
