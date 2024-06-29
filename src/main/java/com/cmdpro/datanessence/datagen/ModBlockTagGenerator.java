package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DataNEssence.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BlockRegistry.ESSENCE_BUFFER.get())
                .add(BlockRegistry.FLUID_BUFFER.get())
                .add(BlockRegistry.ESSENCE_POINT.get())
                .add(BlockRegistry.LUNAR_ESSENCE_POINT.get())
                .add(BlockRegistry.NATURAL_ESSENCE_POINT.get())
                .add(BlockRegistry.EXOTIC_ESSENCE_POINT.get())
                .add(BlockRegistry.ITEM_POINT.get())
                .add(BlockRegistry.FLUID_POINT.get())
                .add(BlockRegistry.FABRICATOR.get())
                .add(BlockRegistry.ITEM_BUFFER.get())
                .add(BlockRegistry.DATA_BANK.get())
                .add(BlockRegistry.POLISHED_OBSIDIAN.get())
                .add(BlockRegistry.POLISHED_OBSIDIAN_COLUMN.get())
                .add(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN.get())
                .add(BlockRegistry.INFUSER.get());
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(BlockRegistry.POLISHED_OBSIDIAN.get())
                .add(BlockRegistry.POLISHED_OBSIDIAN_COLUMN.get())
                .add(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN.get());
    }
}
