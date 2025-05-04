package com.cmdpro.datanessence.integration;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.IChemicalHandler;
import net.mariu73.opalescence.block.OpalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class DataNEssenceIntegration {
    public static boolean hasMekanism;
    public static boolean hasOpalescence;

    public static BlockCapability<IChemicalHandler, Direction> BLOCK_CHEMICAL = hasMekanism ? BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "chemical_handler"), IChemicalHandler.class) : null;
    public static void init() {
        hasMekanism = ModList.get().isLoaded("mekanism");
        hasOpalescence = ModList.get().isLoaded("opalescence");
    }
    public static class OpalescenseIntegration {
        public static int getOpalBlockColor(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos, int tintIndex) {
            return OpalBlock.getBlockColor(state, blockAndTintGetter, pos, tintIndex);
        }
        public static int getOpalItemColor(ItemStack stack, int tintIndex) {
            return OpalBlock.getItemColor(stack, tintIndex);
        }
    }
}
