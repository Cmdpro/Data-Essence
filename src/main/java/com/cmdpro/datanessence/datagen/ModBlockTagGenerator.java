package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.init.BlockInit;
import com.cmdpro.datanessence.init.TagInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
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
                .add(BlockInit.ESSENCE_BUFFER.get())
                .add(BlockInit.FLUID_BUFFER.get())
                .add(BlockInit.ESSENCE_POINT.get())
                .add(BlockInit.LUNAR_ESSENCE_POINT.get())
                .add(BlockInit.NATURAL_ESSENCE_POINT.get())
                .add(BlockInit.EXOTIC_ESSENCE_POINT.get())
                .add(BlockInit.ITEM_POINT.get())
                .add(BlockInit.FLUID_POINT.get())
                .add(BlockInit.FABRICATOR.get())
                .add(BlockInit.ITEM_BUFFER.get())
                .add(BlockInit.DATA_BANK.get())
                .add(BlockInit.POLISHED_OBSIDIAN.get())
                .add(BlockInit.POLISHED_OBSIDIAN_COLUMN.get())
                .add(BlockInit.ENGRAVED_POLISHED_OBSIDIAN.get());
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(BlockInit.POLISHED_OBSIDIAN.get())
                .add(BlockInit.POLISHED_OBSIDIAN_COLUMN.get())
                .add(BlockInit.ENGRAVED_POLISHED_OBSIDIAN.get());
    }
}
