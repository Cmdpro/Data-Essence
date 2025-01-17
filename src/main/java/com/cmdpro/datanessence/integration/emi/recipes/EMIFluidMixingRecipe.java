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

import java.util.*;

public class EMIFluidMixingRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final EmiIngredient item;
    private final EmiStack fluid1;
    private final EmiStack fluid2;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> output;
    private final float essenceCost;

    public EMIFluidMixingRecipe(ResourceLocation id, FluidMixingRecipe recipe) {
        this.id = id;
        this.fluid1 = EmiStack.of(recipe.getInput1().getFluid(), recipe.getInput1().getAmount());
        this.fluid2 = recipe.getInput2() == null ? null : EmiStack.of(recipe.getInput2().getFluid(), recipe.getInput2().getAmount());
        this.item = recipe.getIngredients().isEmpty() ? null : EmiIngredient.of(recipe.getIngredients().get(0));
        this.inputs = new ArrayList<>() {
            {
                add(fluid1);
                if (fluid2 != null) {
                    add(fluid2);
                }
                if (item != null) {
                    add(item);
                }
            }
        };
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
        return inputs;
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
        widgetHolder.addSlot(fluid1, 13, 10).drawBack(false);
        if (fluid2 != null) {
            widgetHolder.addSlot(fluid2, 13, 32).drawBack(false);
        }
        if (item != null) {
            widgetHolder.addSlot(item, 34, 21).drawBack(false);
        }
        // Output
        widgetHolder.addSlot(output.get(0), 77, 21).recipeContext(this).drawBack(false);

        // Essence bars
        widgetHolder.add(new EssenceBarWidget(5, 19, EssenceTypeRegistry.ESSENCE.get(), essenceCost));
    }
}
