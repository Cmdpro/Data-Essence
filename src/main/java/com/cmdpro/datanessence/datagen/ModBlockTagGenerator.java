package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.TagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DataNEssence.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BlockRegistry.AETHER_RUNE.get())
                .add(BlockRegistry.ANCIENT_LANTERN.get())
                .add(BlockRegistry.ANCIENT_ROCK_BRICKS.get())
                .add(BlockRegistry.ANCIENT_ROCK_COLUMN.get())
                .add(BlockRegistry.ANCIENT_ROCK_TILES.get())
                .add(BlockRegistry.ANCIENT_SHELF.get())
                .add(BlockRegistry.ANCIENT_WINDOW.get())
                .add(BlockRegistry.AUTO_FABRICATOR.get())
                .add(BlockRegistry.CHARGER.get())
                .add(BlockRegistry.COMPUTER.get())
                .add(BlockRegistry.CRYOCHAMBER.get())
                .add(BlockRegistry.CRYOCHAMBER_FILLER.get())
                .add(BlockRegistry.DATA_BANK.get())
                .add(BlockRegistry.DECO_ESSENCE_BUFFER.get())
                .add(BlockRegistry.DECO_FLUID_BUFFER.get())
                .add(BlockRegistry.DECO_ITEM_BUFFER.get())
                .add(BlockRegistry.ENERGIZED_ANCIENT_ROCK_COLUMN.get())
                .add(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN.get())
                .add(BlockRegistry.ENTICING_LURE.get())
                .add(BlockRegistry.ENTROPIC_PROCESSOR.get())
                .add(BlockRegistry.ESSENCE_BATTERY.get())
                .add(BlockRegistry.ESSENCE_BREAKER.get())
                .add(BlockRegistry.ESSENCE_BUFFER.get())
                .add(BlockRegistry.ESSENCE_BURNER.get())
                .add(BlockRegistry.ESSENCE_CRYSTAL.get())
                .add(BlockRegistry.ESSENCE_FURNACE.get())
                .add(BlockRegistry.ESSENCE_LEECH.get())
                .add(BlockRegistry.ESSENCE_POINT.get())
                .add(BlockRegistry.EXOTIC_ESSENCE_BATTERY.get())
                .add(BlockRegistry.EXOTIC_ESSENCE_POINT.get())
                .add(BlockRegistry.FABRICATOR.get())
                .add(BlockRegistry.FLUID_BOTTLER.get())
                .add(BlockRegistry.FLUID_BUFFER.get())
                .add(BlockRegistry.FLUID_COLLECTOR.get())
                .add(BlockRegistry.FLUID_POINT.get())
                .add(BlockRegistry.FLUID_MIXER.get())
                .add(BlockRegistry.FLUID_SPILLER.get())
                .add(BlockRegistry.FLUID_TANK.get())
                .add(BlockRegistry.INFUSER.get())
                .add(BlockRegistry.ITEM_BUFFER.get())
                .add(BlockRegistry.ITEM_FILTER.get())
                .add(BlockRegistry.ITEM_POINT.get())
                .add(BlockRegistry.LASER_EMITTER.get())
                .add(BlockRegistry.LENSING_CRYSTAL_ORE.get())
                .add(BlockRegistry.LIMITED_ITEM_BUFFER.get())
                .add(BlockRegistry.LUNAR_ESSENCE_BATTERY.get())
                .add(BlockRegistry.LUNAR_ESSENCE_POINT.get())
                .add(BlockRegistry.MINERAL_PURIFICATION_CHAMBER.get())
                .add(BlockRegistry.NATURAL_ESSENCE_BATTERY.get())
                .add(BlockRegistry.NATURAL_ESSENCE_POINT.get())
                .add(BlockRegistry.PATTERNED_COPPER.get())
                .add(BlockRegistry.POLISHED_OBSIDIAN.get())
                .add(BlockRegistry.POLISHED_OBSIDIAN_COLUMN.get())
                .add(BlockRegistry.SYNTHESIS_CHAMBER.get())
                .add(BlockRegistry.TRAVERSITE_ROAD.get())
                .add(BlockRegistry.VACUUM.get())
                .add(BlockRegistry.INDUSTRIAL_PLANT_SIPHON.get())
                .add(BlockRegistry.MELTER.get());
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(BlockRegistry.POLISHED_OBSIDIAN.get())
                .add(BlockRegistry.POLISHED_OBSIDIAN_COLUMN.get())
                .add(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN.get())
                .add(BlockRegistry.LENSING_CRYSTAL_ORE.get());
        this.tag(TagRegistry.Blocks.BUFFER_DETECTION_PASS)
                .add(BlockRegistry.ITEM_BUFFER.get())
                .add(BlockRegistry.FLUID_BUFFER.get())
                .add(BlockRegistry.ESSENCE_BUFFER.get())
                .add(BlockRegistry.LIMITED_ITEM_BUFFER.get());
    }
}
