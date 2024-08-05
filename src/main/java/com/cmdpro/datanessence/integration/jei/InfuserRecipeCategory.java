package com.cmdpro.datanessence.integration.jei;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.registry.ItemRegistry;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.recipe.InfusionRecipe;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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

public class InfuserRecipeCategory implements IRecipeCategory<InfusionRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "infusion");
    public final static ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/data_tablet_crafting.png");
    private final IDrawable background;
    private final IDrawable icon;

    public InfuserRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(TEXTURE, 10, 136, 123, 60);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemRegistry.INFUSER_ITEM.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InfusionRecipe recipe, IFocusGroup focuses) {

        // Initialize recipe output
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 82, 22);
        List<ItemStack> outputs = List.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
        if (outputs != null) {
            outputSlot.addIngredients(VanillaTypes.ITEM_STACK, outputs);
        }

        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 38, 22);
        if (!recipe.getIngredients().isEmpty()) {
            inputSlot.addIngredients(recipe.getIngredients().get(0));
        }
    }

    @Override
    public void draw(InfusionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBarTiny(guiGraphics, 5, 28-22, 0, recipe.getEssenceCost(), 1000);
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBarTiny(guiGraphics, 13, 28-22, 1, recipe.getLunarEssenceCost(), 1000);
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBarTiny(guiGraphics, 5, 54-22, 2, recipe.getNaturalEssenceCost(), 1000);
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBarTiny(guiGraphics, 13, 54-22, 3, recipe.getExoticEssenceCost(), 1000);
    }

    @Override
    public List<Component> getTooltipStrings(InfusionRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
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
    public RecipeType<InfusionRecipe> getRecipeType() {
        return JEIDataNEssencePlugin.INFUSION_RECIPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.datanessence.infuser");
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