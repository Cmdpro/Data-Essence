package com.cmdpro.datanessence.integration.jei;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.util.client.ClientEssenceBarUtil;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.ShapelessFabricationRecipe;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import com.cmdpro.datanessence.recipe.InfusionRecipe;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
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
        ClientEssenceBarUtil.drawEssenceBarTiny(guiGraphics, 5, 6, EssenceTypeRegistry.ESSENCE.get(), recipe.getEssenceCost().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()), 0f), 1000);
        ClientEssenceBarUtil.drawEssenceBarTiny(guiGraphics, 13, 6, EssenceTypeRegistry.LUNAR_ESSENCE.get(), recipe.getEssenceCost().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.LUNAR_ESSENCE.get()), 0f), 1000);
        ClientEssenceBarUtil.drawEssenceBarTiny(guiGraphics, 5, 32, EssenceTypeRegistry.NATURAL_ESSENCE.get(), recipe.getEssenceCost().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.NATURAL_ESSENCE.get()), 0f), 1000);
        ClientEssenceBarUtil.drawEssenceBarTiny(guiGraphics, 13, 32, EssenceTypeRegistry.EXOTIC_ESSENCE.get(), recipe.getEssenceCost().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.EXOTIC_ESSENCE.get()), 0f), 1000);
    }
    @Override
    public void getTooltip(ITooltipBuilder builder, InfusionRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltip = new ArrayList<>();
        Component essence = ClientEssenceBarUtil.getEssenceBarTooltipTiny(mouseX, mouseY, 5, 6, EssenceTypeRegistry.ESSENCE.get(), recipe.getEssenceCost().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()), 0f));
        if (essence != null) {
            tooltip.clear();
            tooltip.add(essence);
        }
        Component lunarEssence = ClientEssenceBarUtil.getEssenceBarTooltipTiny(mouseX, mouseY, 13, 6, EssenceTypeRegistry.LUNAR_ESSENCE.get(), recipe.getEssenceCost().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.LUNAR_ESSENCE.get()), 0f));
        if (lunarEssence != null) {
            tooltip.clear();
            tooltip.add(lunarEssence);
        }
        Component naturalEssence = ClientEssenceBarUtil.getEssenceBarTooltipTiny(mouseX, mouseY, 5, 32, EssenceTypeRegistry.NATURAL_ESSENCE.get(), recipe.getEssenceCost().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.NATURAL_ESSENCE.get()), 0f));
        if (naturalEssence != null) {
            tooltip.clear();
            tooltip.add(naturalEssence);
        }
        Component exoticEssence = ClientEssenceBarUtil.getEssenceBarTooltipTiny(mouseX, mouseY, 13, 32, EssenceTypeRegistry.EXOTIC_ESSENCE.get(), recipe.getEssenceCost().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.EXOTIC_ESSENCE.get()), 0f));
        if (exoticEssence != null) {
            tooltip.clear();
            tooltip.add(exoticEssence);
        }
        builder.addAll(tooltip);
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