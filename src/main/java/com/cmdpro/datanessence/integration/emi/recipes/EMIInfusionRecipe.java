package com.cmdpro.datanessence.integration.emi.recipes;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.integration.emi.EMIDataNEssencePlugin;
import com.cmdpro.datanessence.integration.emi.widgets.EssenceBarWidget;
import com.cmdpro.datanessence.recipe.InfusionRecipe;

import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EMIInfusionRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final Map<EssenceType, Float> essenceCost;

    public EMIInfusionRecipe(ResourceLocation id, InfusionRecipe recipe) {
        this.id = id;
        this.input = recipe.getIngredients().stream().map(s -> EmiIngredient.of(Arrays.stream(s.getItems()).map(EmiStack::of).toList())).toList();
        this.output = List.of(EmiStack.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess())));
        this.essenceCost = new HashMap<>();
        for (Map.Entry<ResourceLocation, Float> i : recipe.getEssenceCost().entrySet()) {
            this.essenceCost.put(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i.getKey()), i.getValue());
        }
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
        ResourceLocation background = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting.png");

        widgetHolder.addTexture(background, 0, 0, getDisplayWidth(), getDisplayHeight(), 10, 136);

        // Input
        widgetHolder.addSlot(input.get(0), 37, 21).drawBack(false);
        // Output
        widgetHolder.addSlot(output.get(0), 81, 21).recipeContext(this).drawBack(false);

        // Essence bars
        widgetHolder.add(new EssenceBarWidget(5, 6, EssenceTypeRegistry.ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.ESSENCE.get(), 0f)));
        widgetHolder.add(new EssenceBarWidget(13, 6, EssenceTypeRegistry.LUNAR_ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.LUNAR_ESSENCE.get(), 0f)));
        widgetHolder.add(new EssenceBarWidget(5, 32, EssenceTypeRegistry.NATURAL_ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.NATURAL_ESSENCE.get(), 0f)));
        widgetHolder.add(new EssenceBarWidget(13, 32, EssenceTypeRegistry.EXOTIC_ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.EXOTIC_ESSENCE.get(), 0f)));
    }
}
