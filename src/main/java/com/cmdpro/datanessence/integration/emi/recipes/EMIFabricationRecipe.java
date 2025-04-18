package com.cmdpro.datanessence.integration.emi.recipes;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.integration.emi.DataNEssenceEMIRecipe;
import com.cmdpro.datanessence.integration.emi.DataNEssenceEMIPlugin;
import com.cmdpro.datanessence.integration.emi.widgets.EssenceBarWidget;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.ShapedFabricationRecipe;
import com.cmdpro.datanessence.recipe.ShapelessFabricationRecipe;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.screen.DataTabletScreen;

import com.google.common.collect.Lists;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EMIFabricationRecipe extends DataNEssenceEMIRecipe {
    private final boolean shapeless;
    private final Map<EssenceType, Float> essenceCost;
    private final int width;
    private final int height;

    public EMIFabricationRecipe(ResourceLocation id, IFabricationRecipe recipe) {
        super(DataNEssenceEMIPlugin.FABRICATION, id, recipe.getEntry(), 123, 60);

        this.shapeless = recipe instanceof ShapelessFabricationRecipe;
        this.inputs = getIngredients(recipe, shapeless);
        this.outputs = List.of( EmiStack.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess())) );

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

    private static List<EmiIngredient> getIngredients(IFabricationRecipe recipe, boolean isShapeless) {
        List<EmiIngredient> input;
        if (isShapeless) {
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
        return input;
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
        if (inputs.size() > 9) {
            return false;
        }
        for (int i = 0; i < inputs.size(); i++) {
            int x = i % 3;
            int y = i / 3;
            if (!inputs.get(i).isEmpty() && (x >= width || y >= height)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addUnlockedWidgets(WidgetHolder widgetHolder) {
        ResourceLocation background = DataNEssence.locate("textures/gui/data_tablet_crafting.png");

        widgetHolder.addTexture(background, 0, 0, getDisplayWidth(), getDisplayHeight(), 10, 196);

        // Recipe output
        widgetHolder.addSlot(outputs.get(0), 97, 21).recipeContext(this).drawBack(false);

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
            if (s >= 0 && s < inputs.size()) {
                widgetHolder.addSlot(inputs.get(s), (i % 3 * 17) + 20, (i / 3 * 17) + 4).drawBack(false);
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
