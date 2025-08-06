package com.cmdpro.datanessence.integration.emi.recipes;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceBarBackgroundTypes;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.integration.emi.DataNEssenceEMIPlugin;
import com.cmdpro.datanessence.integration.emi.DataNEssenceEMIRecipe;
import com.cmdpro.datanessence.integration.emi.widgets.EssenceBarWidget;
import com.cmdpro.datanessence.recipe.SynthesisRecipe;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EMISynthesisRecipe extends DataNEssenceEMIRecipe {
    private final Map<EssenceType, Float> essenceCost;
    private final int time;

    public EMISynthesisRecipe(ResourceLocation id, SynthesisRecipe recipe) {
        super(DataNEssenceEMIPlugin.SYNTHESIS, id, recipe.getEntry(), 123, 60);

        this.inputs = recipe.getIngredients().stream().map(s -> EmiIngredient.of(Arrays.stream(s.getItems()).map(EmiStack::of).toList())).toList();
        this.outputs = List.of(EmiStack.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess())));
        this.essenceCost = new HashMap<>();
        for (Map.Entry<ResourceLocation, Float> i : recipe.getEssenceCost().entrySet()) {
            this.essenceCost.put(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i.getKey()), i.getValue());
        }
        this.time = recipe.getTime();
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
    public void addUnlockedWidgets(WidgetHolder widgetHolder) {
        ResourceLocation background = DataNEssence.locate("textures/gui/data_tablet_crafting.png");

        widgetHolder.addTexture(background, 0, 0, getDisplayWidth(), getDisplayHeight(), 133, 76);

        // Input
        widgetHolder.addSlot(inputs.get(0), 37, 10).drawBack(false);
        widgetHolder.addSlot(inputs.get(1), 37, 32).drawBack(false);
        // Output
        widgetHolder.addSlot(outputs.get(0), 81, 21).recipeContext(this).drawBack(false);
        widgetHolder.addText(Component.translatable("emi.datanessence.time_seconds", (double) time / 20), 90, 44, 0xffffff, false).horizontalAlign(TextWidget.Alignment.CENTER);

        // Essence bars
        widgetHolder.add(new EssenceBarWidget(5, 6, EssenceTypeRegistry.ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.ESSENCE.get(), 0f), 1000f, EssenceBarBackgroundTypes.INDUSTRIAL));
        widgetHolder.add(new EssenceBarWidget(13, 6, EssenceTypeRegistry.LUNAR_ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.LUNAR_ESSENCE.get(), 0f), 1000f, EssenceBarBackgroundTypes.INDUSTRIAL));
        widgetHolder.add(new EssenceBarWidget(5, 32, EssenceTypeRegistry.NATURAL_ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.NATURAL_ESSENCE.get(), 0f), 1000f, EssenceBarBackgroundTypes.INDUSTRIAL));
        widgetHolder.add(new EssenceBarWidget(13, 32, EssenceTypeRegistry.EXOTIC_ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.EXOTIC_ESSENCE.get(), 0f), 1000f, EssenceBarBackgroundTypes.INDUSTRIAL));
    }
}
