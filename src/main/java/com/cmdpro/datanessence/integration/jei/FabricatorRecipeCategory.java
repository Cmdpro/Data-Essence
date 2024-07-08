package com.cmdpro.datanessence.integration.jei;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.ItemRegistry;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.ShapelessFabricationRecipe;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class FabricatorRecipeCategory implements IRecipeCategory<IFabricationRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(DataNEssence.MOD_ID, "fabrication");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final ICraftingGridHelper craftingGridHelper;

    public FabricatorRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(TEXTURE, 10, 196, 123, 60);
        this.craftingGridHelper = guiHelper.createCraftingGridHelper();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemRegistry.FABRICATOR_ITEM.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFabricationRecipe recipe, IFocusGroup focuses) {

        // Initialize recipe output
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 22);
        List<ItemStack> outputs = List.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
        if (outputs != null) {
            outputSlot.addIngredients(VanillaTypes.ITEM_STACK, outputs);
        }

        // Initialize recipe inputs
        int width = (recipe instanceof IShapedRecipe<?> shapedRecipe) ? shapedRecipe.getRecipeWidth() : 0;
        int height = (recipe instanceof IShapedRecipe<?> shapedRecipe) ? shapedRecipe.getRecipeHeight() : 0;
        List<List<ItemStack>> inputs = recipe.getIngredients().stream().map(ingredient -> List.of(ingredient.getItems())).toList();

        List<IRecipeSlotBuilder> inputSlots = new ArrayList<>();
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x * 17 + 21, y * 17 + 5);
                inputSlots.add(slot);
            }
        }
        craftingGridHelper.setInputs(inputSlots, VanillaTypes.ITEM_STACK, inputs, width, height);
    }

    @Override
    public void draw(IFabricationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (recipe.getEssenceCost() > 0) {
            guiGraphics.blit(DataTabletScreen.TEXTURECRAFTING, 5, 28-(int)Math.ceil(22f*(recipe.getEssenceCost()/1000f)), 6, 224-(int)Math.ceil(22f*(recipe.getEssenceCost()/1000f)), 3, (int)Math.ceil(22f*(recipe.getEssenceCost()/1000f)));
        }
        if (recipe.getLunarEssenceCost() > 0) {
            guiGraphics.blit(DataTabletScreen.TEXTURECRAFTING, 13, 28-(int)Math.ceil(22f*(recipe.getLunarEssenceCost()/1000f)), 1, 224-(int)Math.ceil(22f*(recipe.getLunarEssenceCost()/1000f)), 3, (int)Math.ceil(22f*(recipe.getLunarEssenceCost()/1000f)));
        }
        if (recipe.getNaturalEssenceCost() > 0) {
            guiGraphics.blit(DataTabletScreen.TEXTURECRAFTING, 5, 54-(int)Math.ceil(22f*(recipe.getNaturalEssenceCost()/1000f)), 6, 248-(int)Math.ceil(22f*(recipe.getNaturalEssenceCost()/1000f)), 3, (int)Math.ceil(22f*(recipe.getNaturalEssenceCost()/1000f)));
        }
        if (recipe.getExoticEssenceCost() > 0) {
            guiGraphics.blit(DataTabletScreen.TEXTURECRAFTING, 13, 54-(int)Math.ceil(22f*(recipe.getExoticEssenceCost()/1000f)), 1, 248-(int)Math.ceil(22f*(recipe.getExoticEssenceCost()/1000f)), 3, (int)Math.ceil(22f*(recipe.getExoticEssenceCost()/1000f)));
        }
        if (recipe instanceof ShapelessFabricationRecipe) {
            guiGraphics.blit(DataTabletScreen.TEXTURECRAFTING, 93, 4, 242, 185, 14, 11);
        }
    }

    @Override
    public List<Component> getTooltipStrings(IFabricationRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltip = new ArrayList<>();
        if (recipe.getEssenceCost() > 0) {
            if (mouseX >= 5 && mouseY >= (28-22)) {
                if (mouseX <= 8 && mouseY <= 28) {
                    tooltip.clear();
                    if (ClientPlayerData.getUnlockedEssences()[0]) {
                        tooltip.add(Component.translatable("item.datanessence.data_tablet.page_type.crafting.fabricator.essence", recipe.getEssenceCost()));
                    } else {
                        tooltip.add(Component.translatable("item.datanessence.data_tablet.page_type.crafting.fabricator.unknown", recipe.getEssenceCost()));
                    }
                }
            }
        }
        if (recipe.getLunarEssenceCost() > 0) {
            if (mouseX >= 13 && mouseY >= (28-22)) {
                if (mouseX <= 16 && mouseY <= 28) {
                    tooltip.clear();
                    if (ClientPlayerData.getUnlockedEssences()[1]) {
                        tooltip.add(Component.translatable("item.datanessence.data_tablet.page_type.crafting.fabricator.lunar_essence", recipe.getLunarEssenceCost()));
                    } else {
                        tooltip.add(Component.translatable("item.datanessence.data_tablet.page_type.crafting.fabricator.unknown", recipe.getLunarEssenceCost()));
                    }
                }
            }
        }
        if (recipe.getNaturalEssenceCost() > 0) {
            if (mouseX >= 5 && mouseY >= (54-22)) {
                if (mouseX <= 8 && mouseY <= 54) {
                    tooltip.clear();
                    if (ClientPlayerData.getUnlockedEssences()[2]) {
                        tooltip.add(Component.translatable("item.datanessence.data_tablet.page_type.crafting.fabricator.natural_essence", recipe.getNaturalEssenceCost()));
                    } else {
                        tooltip.add(Component.translatable("item.datanessence.data_tablet.page_type.crafting.fabricator.unknown", recipe.getNaturalEssenceCost()));
                    }
                }
            }
        }
        if (recipe.getExoticEssenceCost() > 0) {
            if (mouseX >= 13 && mouseY >= (54-22)) {
                if (mouseX <= 16 && mouseY <= 54) {
                    tooltip.clear();
                    if (ClientPlayerData.getUnlockedEssences()[3]) {
                        tooltip.add(Component.translatable("item.datanessence.data_tablet.page_type.crafting.fabricator.exotic_essence", recipe.getExoticEssenceCost()));
                    } else {
                        tooltip.add(Component.translatable("item.datanessence.data_tablet.page_type.crafting.fabricator.unknown", recipe.getExoticEssenceCost()));
                    }
                }
            }
        }
        return tooltip;
    }

    @Override
    public RecipeType<IFabricationRecipe> getRecipeType() {
        return JEIDataNEssencePlugin.FABRICATION_RECIPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.datanessence.fabricator");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}