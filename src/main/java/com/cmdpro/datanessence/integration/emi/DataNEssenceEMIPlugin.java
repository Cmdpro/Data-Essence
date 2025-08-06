package com.cmdpro.datanessence.integration.emi;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.integration.emi.recipes.*;
import com.cmdpro.datanessence.recipe.*;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@EmiEntrypoint
public class DataNEssenceEMIPlugin implements EmiPlugin {
    public static final ResourceLocation EMI_ICONS = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"textures/gui/emi_icons.png");

    public static final EmiStack FABRICATOR_WORKSTATION = EmiStack.of(BlockRegistry.FABRICATOR.get());
    public static final EmiStack LUNARIUM_WORKSTATION = EmiStack.of(BlockRegistry.LUNARIUM.get());
    public static final EmiStack AUTO_FABRICATOR_WORKSTATION = EmiStack.of(BlockRegistry.AUTO_FABRICATOR.get());
    public static final EmiStack INFUSER_WORKSTATION = EmiStack.of(BlockRegistry.INFUSER.get());
    public static final EmiStack ENTROPIC_PROCESSER_WORKSTATION = EmiStack.of(BlockRegistry.ENTROPIC_PROCESSOR.get());
    public static final EmiStack FLUID_MIXER_WORKSTATION = EmiStack.of(BlockRegistry.FLUID_MIXER.get());
    public static final EmiStack SYNTHESIS_CHAMBER_WORKSTATION = EmiStack.of(BlockRegistry.SYNTHESIS_CHAMBER.get());
    public static final EmiStack METAL_SHAPER_WORKSTATION = EmiStack.of(BlockRegistry.METAL_SHAPER.get());
    public static final EmiStack MELTER_WORKSTATION = EmiStack.of(BlockRegistry.MELTER.get());
    public static final EmiStack DRYING_TABLE_WORKSTATION = EmiStack.of(BlockRegistry.DRYING_TABLE.get());
    public static final EmiStack MINERAL_PURIFICATION_CHAMBER_WORKSTATION = EmiStack.of(BlockRegistry.MINERAL_PURIFICATION_CHAMBER.get());

    public static final EmiRecipeCategory FABRICATION = new EmiRecipeCategory(DataNEssence.locate("fabrication"), FABRICATOR_WORKSTATION, new EmiTexture(EMI_ICONS, 0, 0, 16, 16));
    public static final EmiRecipeCategory INFUSION = new EmiRecipeCategory(DataNEssence.locate("infusion"), INFUSER_WORKSTATION, new EmiTexture(EMI_ICONS, 16, 0, 16, 16));
    public static final EmiRecipeCategory ENTROPIC_PROCESSING = new EmiRecipeCategory(DataNEssence.locate("entropic_processing"), ENTROPIC_PROCESSER_WORKSTATION, new EmiTexture(EMI_ICONS, 32, 0, 16, 16));
    public static final EmiRecipeCategory FLUID_MIXING = new EmiRecipeCategory(DataNEssence.locate("fluid_mixing"), FLUID_MIXER_WORKSTATION, new EmiTexture(EMI_ICONS, 48, 0, 16, 16));
    public static final EmiRecipeCategory SYNTHESIS = new EmiRecipeCategory(DataNEssence.locate("synthesis"), SYNTHESIS_CHAMBER_WORKSTATION, new EmiTexture(EMI_ICONS, 64, 0, 16, 16));
    public static final EmiRecipeCategory METAL_SHAPING = new EmiRecipeCategory(DataNEssence.locate("metal_shaping"), METAL_SHAPER_WORKSTATION, new EmiTexture(EMI_ICONS, 80, 0, 16, 16));
    public static final EmiRecipeCategory MELTING = new EmiRecipeCategory(DataNEssence.locate("melting"), MELTER_WORKSTATION, new EmiTexture(EMI_ICONS, 96, 0, 16, 16));
    public static final EmiRecipeCategory DRYING = new EmiRecipeCategory(DataNEssence.locate("drying"), DRYING_TABLE_WORKSTATION, new EmiTexture(EMI_ICONS, 112, 0, 16, 16));
    public static final EmiRecipeCategory MINERAL_PURIFICATION = new EmiRecipeCategory(DataNEssence.locate("mineral_purification"), MINERAL_PURIFICATION_CHAMBER_WORKSTATION, new EmiTexture(EMI_ICONS, 128, 0, 16, 16));

    // TODO this Essence Furnace should have a custom tooltip, because *polish*
    public static ItemStack ESSENCE_FURNACE = new ItemStack(BlockRegistry.ESSENCE_FURNACE.get().asItem());
    // .set(DataComponents.LORE, new ItemLore(List.of(Component.translatable("data_tablet.pages.essence_furnace.page2.text"))))

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addCategory(FABRICATION);
        emiRegistry.addCategory(INFUSION);
        emiRegistry.addCategory(ENTROPIC_PROCESSING);
        emiRegistry.addCategory(FLUID_MIXING);
        emiRegistry.addCategory(SYNTHESIS);
        emiRegistry.addCategory(METAL_SHAPING);
        emiRegistry.addCategory(MELTING);
        emiRegistry.addCategory(DRYING);
        emiRegistry.addCategory(MINERAL_PURIFICATION);

        // Vanilla
        emiRegistry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, FABRICATOR_WORKSTATION);
        emiRegistry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, AUTO_FABRICATOR_WORKSTATION);
        emiRegistry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiStack.of(ESSENCE_FURNACE));

        // Ours
        emiRegistry.addWorkstation(FABRICATION, FABRICATOR_WORKSTATION);
        emiRegistry.addWorkstation(FABRICATION, LUNARIUM_WORKSTATION);
        emiRegistry.addWorkstation(FABRICATION, AUTO_FABRICATOR_WORKSTATION);
        emiRegistry.addWorkstation(INFUSION, INFUSER_WORKSTATION);
        emiRegistry.addWorkstation(ENTROPIC_PROCESSING, ENTROPIC_PROCESSER_WORKSTATION);
        emiRegistry.addWorkstation(FLUID_MIXING, FLUID_MIXER_WORKSTATION);
        emiRegistry.addWorkstation(SYNTHESIS, SYNTHESIS_CHAMBER_WORKSTATION);
        emiRegistry.addWorkstation(METAL_SHAPING, METAL_SHAPER_WORKSTATION);
        emiRegistry.addWorkstation(MELTING, MELTER_WORKSTATION);
        emiRegistry.addWorkstation(DRYING, DRYING_TABLE_WORKSTATION);
        emiRegistry.addWorkstation(MINERAL_PURIFICATION, MINERAL_PURIFICATION_CHAMBER_WORKSTATION);

        RecipeManager manager = emiRegistry.getRecipeManager();

        for (RecipeHolder<IFabricationRecipe> recipe : manager.getAllRecipesFor(RecipeRegistry.FABRICATIONCRAFTING.get())) {
            emiRegistry.addRecipe(new EMIFabricationRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<InfusionRecipe> recipe : manager.getAllRecipesFor(RecipeRegistry.INFUSION_TYPE.get())) {
            emiRegistry.addRecipe(new EMIInfusionRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<EntropicProcessingRecipe> recipe : manager.getAllRecipesFor(RecipeRegistry.ENTROPIC_PROCESSING_TYPE.get())) {
            emiRegistry.addRecipe(new EMIEntropicProcessingRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<FluidMixingRecipe> recipe : manager.getAllRecipesFor(RecipeRegistry.FLUID_MIXING_TYPE.get())) {
            emiRegistry.addRecipe(new EMIFluidMixingRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<SynthesisRecipe> recipe : manager.getAllRecipesFor(RecipeRegistry.SYNTHESIS_TYPE.get())) {
            emiRegistry.addRecipe(new EMISynthesisRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<MetalShaperRecipe> recipe : manager.getAllRecipesFor(RecipeRegistry.METAL_SHAPING_TYPE.get())) {
            emiRegistry.addRecipe(new EMIMetalShaperRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<MeltingRecipe> recipe : manager.getAllRecipesFor(RecipeRegistry.MELTING_TYPE.get())) {
            emiRegistry.addRecipe(new EMIMeltingRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<DryingRecipe> recipe : manager.getAllRecipesFor(RecipeRegistry.DRYING_TYPE.get())) {
            emiRegistry.addRecipe(new EMIDryingRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<MineralPurificationRecipe> recipe : manager.getAllRecipesFor(RecipeRegistry.MINERAL_PURIFICATION_TYPE.get())) {
            emiRegistry.addRecipe(new EMIMineralPurificationRecipe(recipe.id(), recipe.value()));
        }
    }
}
