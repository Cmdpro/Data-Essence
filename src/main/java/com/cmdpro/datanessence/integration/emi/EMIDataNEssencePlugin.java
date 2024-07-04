package com.cmdpro.datanessence.integration.emi;

import com.cmdpro.datanessence.registry.ItemRegistry;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;

import net.minecraft.resources.ResourceLocation;

@EmiEntrypoint
public class EMIDataNEssencePlugin implements EmiPlugin {
    public static final ResourceLocation EMI_ICONS = new ResourceLocation("datanessence","textures/gui/emi/emi_icons.png");

    public static final EmiStack FABRICATOR_WORKSTATION = EmiStack.of(ItemRegistry.FABRICATOR_ITEM.get());
    public static final EmiStack INFUSER_WORKSTATION = EmiStack.of(ItemRegistry.INFUSER_ITEM.get());

    public static final EmiRecipeCategory FABRICATION = new EmiRecipeCategory(new ResourceLocation("datanessence","fabrication"), FABRICATOR_WORKSTATION, new EmiTexture(EMI_ICONS, 0, 0, 16, 16));
    public static final EmiRecipeCategory INFUSION = new EmiRecipeCategory(new ResourceLocation("datanessence", "infusion"), INFUSER_WORKSTATION, new EmiTexture(EMI_ICONS, 16, 0, 16, 16));

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addWorkstation(FABRICATION, FABRICATOR_WORKSTATION);
        emiRegistry.addWorkstation(INFUSION, INFUSER_WORKSTATION);
    }
}
