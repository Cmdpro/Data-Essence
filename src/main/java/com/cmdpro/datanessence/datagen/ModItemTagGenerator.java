package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.TagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_,
                               CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, DataNEssence.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(TagRegistry.Items.LOW_ESSENCE_PLANTS)
                .addTag(Tags.Items.CROPS)
                .addTag(ItemTags.LEAVES);
        this.tag(TagRegistry.Items.MEDIUM_ESSENCE_PLANTS)
                .add(BlockRegistry.TETHERGRASS.get().asItem());
        this.tag(TagRegistry.Items.HIGH_ESSENCE_PLANTS)
                .add(Items.CHORUS_FRUIT);
        this.tag(TagRegistry.Items.EXPLOSIVE_MATERIAL)
                .add(Items.GUNPOWDER)
                .addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "dusts/sulfur"));
        this.tag(TagRegistry.Items.SHAPED_POLISHED_OBSIDIAN_BLOCKS)
                .add(BlockRegistry.POLISHED_OBSIDIAN_COLUMN.get().asItem())
                .add(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN.get().asItem());
        this.tag(TagRegistry.Items.POLISHED_OBSIDIAN_BLOCKS)
                .addTag(TagRegistry.Items.SHAPED_POLISHED_OBSIDIAN_BLOCKS)
                .add(BlockRegistry.POLISHED_OBSIDIAN.get().asItem());
    }
}