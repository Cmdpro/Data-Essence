package com.cmdpro.datanessence.integration.emi.recipes;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.integration.emi.EMIDataNEssencePlugin;
import com.cmdpro.datanessence.integration.emi.widgets.EssenceBarWidget;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.ShapelessFabricationRecipe;
import com.cmdpro.datanessence.screen.DataTabletScreen;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EMIFabricationRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final EmiStack output;
    private final boolean shapeless;
    private final float essenceCost;
    private final float lunarEssenceCost;
    private final float naturalEssenceCost;
    private final float exoticEssenceCost;

    public EMIFabricationRecipe(ResourceLocation id, IFabricationRecipe recipe) {
        this.id = id;
        this.shapeless = recipe instanceof ShapelessFabricationRecipe;
        this.input = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
        this.output = EmiStack.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
        this.essenceCost = recipe.getEssenceCost();
        this.lunarEssenceCost = recipe.getLunarEssenceCost();
        this.naturalEssenceCost = recipe.getNaturalEssenceCost();
        this.exoticEssenceCost = recipe.getExoticEssenceCost();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMIDataNEssencePlugin.FABRICATION;
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
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 123;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    public boolean canFit(int width, int height) {
        if (input.size() > 9) {
            return false;
        }
        for (int i = 0; i < input.size(); i++) {
            int x = i % 3;
            int y = i / 3;
            if (!input.get(i).isEmpty() && (x >= width || y >= height)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        ResourceLocation background = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting.png");

        widgetHolder.addTexture(background, 0, 0, getDisplayWidth(), getDisplayHeight(), 10, 196);

        // Initialize recipe output
        widgetHolder.addSlot(output, 97, 21).recipeContext(this).drawBack(false);

        int sOff = 0;
        if (!shapeless) {
            if (canFit(1, 3)) {
                sOff -= 1;
            }
            if (canFit(3, 1)) {
                sOff -= 3;
            }
        } else {
            widgetHolder.addTexture(DataTabletScreen.TEXTURECRAFTING, 93, 4, 14, 11, 242, 185);
        }
        int i = 0;
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int s = i + sOff;
                if (s >= 0 && s < input.size()) {
                    widgetHolder.addSlot(input.get(s), x * 17 + 20, y * 17 + 4).drawBack(false);
                } else {
                    widgetHolder.addSlot(EmiStack.of(ItemStack.EMPTY), x * 17 + 20, y * 17 + 4).drawBack(false);
                }
                i++;
            }
        }
        widgetHolder.add(new EssenceBarWidget(5, 6, 0, essenceCost));
        widgetHolder.add(new EssenceBarWidget(13, 6, 1, lunarEssenceCost));
        widgetHolder.add(new EssenceBarWidget(5, 32, 2, naturalEssenceCost));
        widgetHolder.add(new EssenceBarWidget(13, 32, 3, exoticEssenceCost));
    }
}
