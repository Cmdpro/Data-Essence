package com.cmdpro.datanessence.datagen.datamap;

import com.cmdpro.datanessence.datamaps.DataNEssenceDatamaps;
import com.cmdpro.datanessence.datamaps.PlantSiphonEssenceMap;
import com.cmdpro.datanessence.registry.BlockRegistry;
import earth.terrarium.pastel.registries.PastelBlocks;
import earth.terrarium.pastel.registries.PastelItemTags;
import earth.terrarium.pastel.registries.PastelItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class PlantSiphonEssenceProvider extends DataMapProvider {

    public PlantSiphonEssenceProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        this.builder(DataNEssenceDatamaps.PLANT_SIPHON_ESSENCE)
                .add(Tags.Items.CROPS, new PlantSiphonEssenceMap(20, 2), false)
                .add(Tags.Items.SEEDS, new PlantSiphonEssenceMap(10, 2), false)
                .add(ItemTags.SAPLINGS, new PlantSiphonEssenceMap(10, 2), false)
                .add(ItemTags.LEAVES, new PlantSiphonEssenceMap(20, 2), false)
                .add(ItemTags.FLOWERS, new PlantSiphonEssenceMap(10, 2), false)
                .add(Items.BAMBOO.builtInRegistryHolder(), new PlantSiphonEssenceMap(10, 2), false)
                .add(Items.KELP.builtInRegistryHolder(), new PlantSiphonEssenceMap(10, 2), false)
                .add(Items.SEA_PICKLE.builtInRegistryHolder(), new PlantSiphonEssenceMap(10, 2), false)

                .add(Items.WITHER_ROSE.builtInRegistryHolder(), new PlantSiphonEssenceMap(50, 6), false)
                .add(Items.CHORUS_FLOWER.builtInRegistryHolder(), new PlantSiphonEssenceMap(60, 6), false)
                .add(Items.CHORUS_FRUIT.builtInRegistryHolder(), new PlantSiphonEssenceMap(50, 4), false)
                .add(Items.SPORE_BLOSSOM.builtInRegistryHolder(), new PlantSiphonEssenceMap(50, 4), false)
                .add(Items.PITCHER_PLANT.builtInRegistryHolder(), new PlantSiphonEssenceMap(50, 4), false)
                .add(Items.TORCHFLOWER.builtInRegistryHolder(), new PlantSiphonEssenceMap(50, 4), false)
                .add(PastelItems.AMARANTH_GRAINS, new PlantSiphonEssenceMap(25, 4), false, new ModLoadedCondition("pastel"))
                .add(PastelBlocks.AMARANTH_BUSHEL.asItem().builtInRegistryHolder(), new PlantSiphonEssenceMap(50, 4), false, new ModLoadedCondition("pastel"))

                .add(BlockRegistry.TETHERGRASS.get().asItem().builtInRegistryHolder(), new PlantSiphonEssenceMap(100, 8), false)
                .add(PastelItems.VEGETAL, new PlantSiphonEssenceMap(100, 8), false, new ModLoadedCondition("pastel"))
                .add(PastelItems.GERMINATED_JADE_VINE_BULB, new PlantSiphonEssenceMap(100, 8), false, new ModLoadedCondition("pastel"))
                .add(PastelBlocks.JADEITE_LOTUS_BULB.asItem().builtInRegistryHolder(), new PlantSiphonEssenceMap(100, 8), false, new ModLoadedCondition("pastel"))
                .add(PastelBlocks.JADEITE_LOTUS_FLOWER.asItem().builtInRegistryHolder(), new PlantSiphonEssenceMap(100, 8), false, new ModLoadedCondition("pastel"))
                .add(PastelBlocks.NEPHRITE_BLOSSOM_BULB.asItem().builtInRegistryHolder(), new PlantSiphonEssenceMap(100, 8), false, new ModLoadedCondition("pastel"));
    }
}
