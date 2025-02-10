package com.cmdpro.datanessence.integration.emi.recipes;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.integration.emi.EMIDataNEssencePlugin;
import com.cmdpro.datanessence.integration.emi.widgets.EssenceBarWidget;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.ShapedFabricationRecipe;
import com.cmdpro.datanessence.recipe.ShapelessFabricationRecipe;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.screen.DataTabletScreen;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EMIFabricationRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final EmiStack output;
    private final boolean shapeless;
    private final Map<EssenceType, Float> essenceCost;
    private final int width;
    private final int height;

    public EMIFabricationRecipe(ResourceLocation id, IFabricationRecipe recipe) {
        this.id = id;
        this.shapeless = recipe instanceof ShapelessFabricationRecipe;
        boolean shapeless = recipe.canCraftInDimensions(1, recipe.getIngredients().size()) && recipe.canCraftInDimensions(recipe.getIngredients().size(), 1);
        List<EmiIngredient> input;
        if (shapeless) {
            input = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
        } else {
            int width = recipe.canCraftInDimensions(2, 3) ? recipe.canCraftInDimensions(1, 3) ? 1 : 2 : 3;
            input = Lists.newArrayList();
            for (int i = 0; i < recipe.getIngredients().size(); i++) {
                input.add(EmiIngredient.of(recipe.getIngredients().get(i)));
                if ((i + 1) % width == 0) {
                    for (int j = width; j < 3; j++) {
                        input.add(EmiStack.EMPTY);
                    }
                }
            }
        }
        this.input = input;
        this.output = EmiStack.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
        this.essenceCost = new HashMap<>();
        for (Map.Entry<ResourceLocation, Float> i : recipe.getEssenceCost().entrySet()) {
            this.essenceCost.put(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i.getKey()), i.getValue());
        }
        if (recipe instanceof ShapedFabricationRecipe recipe1) {
            width = recipe1.getWidth();
            height = recipe1.getHeight();
        } else {
            width = 3;
            height = 3;
        }
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
        ResourceLocation background = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting.png");

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
            widgetHolder.addTexture(DataTabletScreen.TEXTURE_CRAFTING, 93, 4, 14, 11, 242, 185);
        }
        for (int i = 0; i < 9; i++) {
            int s = i + sOff;
            if (s >= 0 && s < input.size()) {
                widgetHolder.addSlot(input.get(s), (i % 3 * 17) + 20, (i / 3 * 17) + 4).drawBack(false);
            } else {
                widgetHolder.addSlot(EmiStack.of(ItemStack.EMPTY), (i % 3 * 17) + 20, (i / 3 * 17) + 4).drawBack(false);
            }
        }
        widgetHolder.add(new EssenceBarWidget(5, 6, EssenceTypeRegistry.ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.ESSENCE.get(), 0f)));
        widgetHolder.add(new EssenceBarWidget(13, 6, EssenceTypeRegistry.LUNAR_ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.LUNAR_ESSENCE.get(), 0f)));
        widgetHolder.add(new EssenceBarWidget(5, 32, EssenceTypeRegistry.NATURAL_ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.NATURAL_ESSENCE.get(), 0f)));
        widgetHolder.add(new EssenceBarWidget(13, 32, EssenceTypeRegistry.EXOTIC_ESSENCE.get(), essenceCost.getOrDefault(EssenceTypeRegistry.EXOTIC_ESSENCE.get(), 0f)));
    }
}
