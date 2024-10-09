package com.cmdpro.datanessence.integration.emi;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.integration.emi.recipes.*;
import com.cmdpro.datanessence.recipe.*;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.Collections;

@EmiEntrypoint
public class EMIDataNEssencePlugin implements EmiPlugin {
    public static final ResourceLocation EMI_ICONS = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"textures/gui/emi_icons.png");

    public static final EmiStack FABRICATOR_WORKSTATION = EmiStack.of(ItemRegistry.FABRICATOR_ITEM.get());
    public static final EmiStack AUTO_FABRICATOR_WORKSTATION = EmiStack.of(ItemRegistry.AUTO_FABRICATOR_ITEM.get());
    public static final EmiStack INFUSER_WORKSTATION = EmiStack.of(ItemRegistry.INFUSER_ITEM.get());
    public static final EmiStack ENTROPIC_PROCESSER_WORKSTATION = EmiStack.of(ItemRegistry.ENTROPIC_PROCESSOR_ITEM.get());
    public static final EmiStack FLUID_MIXER_WORKSTATION = EmiStack.of(BlockRegistry.FLUID_MIXER.get());
    public static final EmiStack SYNTHESIS_CHAMBER_WORKSTATION = EmiStack.of(BlockRegistry.SYNTHESIS_CHAMBER.get());

    public static final EmiRecipeCategory FABRICATION = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"fabrication"), FABRICATOR_WORKSTATION, new EmiTexture(EMI_ICONS, 0, 0, 16, 16));
    public static final EmiRecipeCategory INFUSION = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "infusion"), INFUSER_WORKSTATION, new EmiTexture(EMI_ICONS, 16, 0, 16, 16));
    public static final EmiRecipeCategory ENTROPIC_PROCESSING = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "entropic_processing"), ENTROPIC_PROCESSER_WORKSTATION, new EmiTexture(EMI_ICONS, 32, 0, 16, 16));
    public static final EmiRecipeCategory FLUID_MIXING = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "fluid_mixing"), FLUID_MIXER_WORKSTATION, new EmiTexture(EMI_ICONS, 48, 0, 16, 16));
    public static final EmiRecipeCategory SYNTHESIS = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "synthesis"), SYNTHESIS_CHAMBER_WORKSTATION, new EmiTexture(EMI_ICONS, 64, 0, 16, 16));

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addCategory(FABRICATION);
        emiRegistry.addCategory(INFUSION);
        emiRegistry.addCategory(ENTROPIC_PROCESSING);
        emiRegistry.addCategory(FLUID_MIXING);
        emiRegistry.addCategory(SYNTHESIS);

        emiRegistry.addWorkstation(FABRICATION, FABRICATOR_WORKSTATION);
        emiRegistry.addWorkstation(FABRICATION, AUTO_FABRICATOR_WORKSTATION);
        emiRegistry.addWorkstation(INFUSION, INFUSER_WORKSTATION);
        emiRegistry.addWorkstation(ENTROPIC_PROCESSING, ENTROPIC_PROCESSER_WORKSTATION);
        emiRegistry.addWorkstation(FLUID_MIXING, FLUID_MIXER_WORKSTATION);
        emiRegistry.addWorkstation(SYNTHESIS, SYNTHESIS_CHAMBER_WORKSTATION);

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
    }
}
