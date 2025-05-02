package com.cmdpro.datanessence.integration;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class DataNEssenceIntegration {
    public static boolean hasMekanism = ModList.get().isLoaded("mekanism");
    public static boolean hasOpalescence = ModList.get().isLoaded("opalescence");

    public static BlockCapability<IChemicalHandler, Direction> BLOCK_CHEMICAL = hasMekanism ? BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "chemical_handler"), IChemicalHandler.class) : null;
}
