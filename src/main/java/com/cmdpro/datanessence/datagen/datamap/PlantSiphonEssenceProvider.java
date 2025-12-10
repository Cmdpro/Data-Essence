package com.cmdpro.datanessence.datagen.datamap;

import com.cmdpro.datanessence.datamaps.DataNEssenceDatamaps;
import com.cmdpro.datanessence.datamaps.PlantSiphonEssenceMap;
import com.cmdpro.datanessence.registry.BlockRegistry;
import earth.terrarium.pastel.registries.PastelBlocks;
import earth.terrarium.pastel.registries.PastelItemTags;
import earth.terrarium.pastel.registries.PastelItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
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
                .add(Tags.Items.MUSHROOMS, new PlantSiphonEssenceMap(20, 2), false)
                .add(Tags.Items.SEEDS, new PlantSiphonEssenceMap(10, 2), false)
                .add(ItemTags.SAPLINGS, new PlantSiphonEssenceMap(10, 2), false)
                .add(ItemTags.LEAVES, new PlantSiphonEssenceMap(20, 2), false)
                .add(ItemTags.FLOWERS, new PlantSiphonEssenceMap(10, 2), false)
                .add(getHolder(Items.BAMBOO), new PlantSiphonEssenceMap(10, 2), false)
                .add(getHolder(Items.KELP), new PlantSiphonEssenceMap(10, 2), false)
                .add(getHolder(Items.SEA_PICKLE), new PlantSiphonEssenceMap(10, 2), false)
                .add(getHolder(Items.SWEET_BERRIES), new PlantSiphonEssenceMap(20, 2), false)
                .add(getHolder(Items.GLOW_BERRIES), new PlantSiphonEssenceMap(20, 2), false)
                .add(getHolder(Items.VINE), new PlantSiphonEssenceMap(5, 1), false)

                .add(getHolder(Items.WITHER_ROSE), new PlantSiphonEssenceMap(50, 6), false)
                .add(getHolder(Items.CHORUS_FLOWER), new PlantSiphonEssenceMap(60, 6), false)
                .add(getHolder(Items.CHORUS_FRUIT), new PlantSiphonEssenceMap(50, 4), false)
                .add(getHolder(Items.SPORE_BLOSSOM), new PlantSiphonEssenceMap(50, 4), false)
                .add(getHolder(Items.PITCHER_PLANT), new PlantSiphonEssenceMap(50, 4), false)
                .add(getHolder(Items.TORCHFLOWER), new PlantSiphonEssenceMap(50, 4), false)
                .add(getHolder(Items.NETHER_WART), new PlantSiphonEssenceMap(25, 4), false)
                .add(getHolder(Items.GOLDEN_APPLE), new PlantSiphonEssenceMap(60, 4), false)
                .add(PastelItems.AMARANTH_GRAINS, new PlantSiphonEssenceMap(25, 4), false, new ModLoadedCondition("pastel"))
                .add(getHolder(PastelBlocks.QUITOXIC_REEDS.asItem()), new PlantSiphonEssenceMap(50, 4), false, new ModLoadedCondition("pastel"))
                .add(getHolder(PastelBlocks.AMARANTH_BUSHEL.asItem()), new PlantSiphonEssenceMap(50, 4), false, new ModLoadedCondition("pastel"))
                .add(PastelItems.NIGHTDEW_SPROUT, new PlantSiphonEssenceMap(50, 4), false, new ModLoadedCondition("pastel"))

                .add(getHolder(BlockRegistry.TETHERGRASS.get().asItem()), new PlantSiphonEssenceMap(100, 8), false)
                .add(getHolder(Items.ENCHANTED_GOLDEN_APPLE), new PlantSiphonEssenceMap(100, 16), false)
                .add(PastelItems.VEGETAL, new PlantSiphonEssenceMap(100, 8), false, new ModLoadedCondition("pastel"))
                .add(PastelItems.GERMINATED_JADE_VINE_BULB, new PlantSiphonEssenceMap(100, 12), false, new ModLoadedCondition("pastel"))
                .add(PastelItems.JADE_VINE_PETALS, new PlantSiphonEssenceMap(100, 8), false, new ModLoadedCondition("pastel"))
                .add(getHolder(PastelBlocks.JADEITE_LOTUS_BULB.asItem()), new PlantSiphonEssenceMap(100, 8), false, new ModLoadedCondition("pastel"))
                .add(getHolder(PastelBlocks.JADEITE_LOTUS_FLOWER.asItem()), new PlantSiphonEssenceMap(100, 8), false, new ModLoadedCondition("pastel"))
                .add(getHolder(PastelBlocks.NEPHRITE_BLOSSOM_BULB.asItem()), new PlantSiphonEssenceMap(100, 8), false, new ModLoadedCondition("pastel"))
                .add(PastelItems.NECTARDEW_BURGEON, new PlantSiphonEssenceMap(100, 16), false, new ModLoadedCondition("pastel"));
    }
    private Holder<Item> getHolder(Item item) {
        return BuiltInRegistries.ITEM.wrapAsHolder(item);
    }
}
