package com.cmdpro.datanessence.integration.emi.recipes;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.integration.emi.DataNEssenceEMIPlugin;
import com.cmdpro.datanessence.recipe.MineralPurificationRecipe;
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
import java.util.List;

public class EMIMineralPurificationRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final int time;

    public EMIMineralPurificationRecipe(ResourceLocation id, MineralPurificationRecipe recipe) {
        this.id = id;
        this.input = recipe.getIngredients().stream().map(s -> EmiIngredient.of(Arrays.stream(s.getItems()).map(EmiStack::of).toList())).toList();
        this.output = List.of(EmiStack.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess())), EmiStack.of(recipe.getNuggetOutput(Minecraft.getInstance().level.registryAccess())));
        this.time = recipe.getTime();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return DataNEssenceEMIPlugin.MINERAL_PURIFICATION;
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
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation background = DataNEssence.locate("textures/gui/data_tablet_crafting2.png");
        ResourceLocation emiIcons = DataNEssence.locate("textures/gui/emi_icons.png");

        widgets.addTexture(background, 0, 0, getDisplayWidth(), getDisplayHeight(), 10, 136);

        var primary = output.get(0);
        var secondary = output.get(1);
        var yield = !primary.isEqual(secondary)
                ? primary.getAmount() + ( 0.45 * secondary.getAmount())
                : primary.getAmount() + ( 4.5 * secondary.getAmount());

        // Yield
        widgets.addText(Component.translatable("emi.datanessence.average_yield"), 118, 2, 0xFFFF96B5, false).horizontalAlign(TextWidget.Alignment.END);
        widgets.addText(Component.literal( String.valueOf( yield )), 118, 12, 0xFFFF96B5, false).horizontalAlign(TextWidget.Alignment.END);

        // Input
        widgets.addSlot(input.get(0), 29, 21).drawBack(false);
        // Output
        widgets.addSlot(primary, 73, 14).recipeContext(this).drawBack(false);
        widgets.addSlot(secondary, 73, 38).recipeContext(this).drawBack(false);
        widgets.addTexture(emiIcons, 94, 43, 8, 8, 128, 16).tooltipText(List.of(Component.translatable("emi.datanessence.nugget_mult")));
        widgets.addText(Component.translatable("emi.datanessence.time_seconds", (double) time / 20), 38, 50, 0xffffff, false).horizontalAlign(TextWidget.Alignment.CENTER);

    }
}
