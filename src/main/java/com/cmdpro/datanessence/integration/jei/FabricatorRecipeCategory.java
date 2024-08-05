package com.cmdpro.datanessence.integration.jei;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.recipe.ShapedFabricationRecipe;
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

import java.util.ArrayList;
import java.util.List;

public class FabricatorRecipeCategory implements IRecipeCategory<IFabricationRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "fabrication");
    public final static ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting.png");
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
        int width = (recipe instanceof ShapedFabricationRecipe shapedRecipe) ? shapedRecipe.getWidth() : 0;
        int height = (recipe instanceof ShapedFabricationRecipe shapedRecipe) ? shapedRecipe.getHeight() : 0;
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
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBarTiny(guiGraphics, 5, 6, 0, recipe.getEssenceCost(), 1000);
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBarTiny(guiGraphics, 13, 6, 1, recipe.getLunarEssenceCost(), 1000);
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBarTiny(guiGraphics, 5, 32, 2, recipe.getNaturalEssenceCost(), 1000);
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBarTiny(guiGraphics, 13, 32, 3, recipe.getExoticEssenceCost(), 1000);
        if (recipe instanceof ShapelessFabricationRecipe) {
            guiGraphics.blit(DataTabletScreen.TEXTURECRAFTING, 93, 4, 242, 185, 14, 11);
        }
    }

    @Override
    public List<Component> getTooltipStrings(IFabricationRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltip = new ArrayList<>();
        Component essence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltipTiny(mouseX, mouseY, 5, 6, 0, recipe.getEssenceCost());
        if (essence != null) {
            tooltip.clear();
            tooltip.add(essence);
        }
        Component lunarEssence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltipTiny(mouseX, mouseY, 13, 6, 1, recipe.getLunarEssenceCost());
        if (lunarEssence != null) {
            tooltip.clear();
            tooltip.add(lunarEssence);
        }
        Component naturalEssence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltipTiny(mouseX, mouseY, 5, 32, 2, recipe.getNaturalEssenceCost());
        if (naturalEssence != null) {
            tooltip.clear();
            tooltip.add(naturalEssence);
        }
        Component exoticEssence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltipTiny(mouseX, mouseY, 13, 32, 3, recipe.getExoticEssenceCost());
        if (exoticEssence != null) {
            tooltip.clear();
            tooltip.add(exoticEssence);
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