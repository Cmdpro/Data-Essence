package com.cmdpro.datanessence.recipe;

import net.minecraft.resources.ResourceLocation;

public interface IHasRequiredKnowledge {
    public ResourceLocation getEntry();
    public boolean allowIncomplete();
    public boolean showIncompleteInEMI();
}
