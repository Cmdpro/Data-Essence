package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
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
        this.tag(Tags.Items.TOOLS_WRENCH)
                .add(ItemRegistry.ESSENCE_REDIRECTOR.get());

        this.tag(Tags.Items.ARMORS)
                .add(ItemRegistry.PRIMITIVE_ANTI_GRAVITY_PACK.get())
                .add(ItemRegistry.ANTI_GRAVITY_PACK.get())
                .add(ItemRegistry.TRAVERSITE_TRUDGERS.get());

        this.tag(ItemTags.FREEZE_IMMUNE_WEARABLES)
                .add(ItemRegistry.TRAVERSITE_TRUDGERS.get());

        this.tag(TagRegistry.Items.EXPLOSIVE_MATERIAL)
                .add(Items.GUNPOWDER)
                .addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "dusts/sulfur"));

        this.tag(TagRegistry.Items.COPPER_PARTS)
                .add(ItemRegistry.CAPACITANCE_PANEL.get())
                .add(ItemRegistry.CONDUCTANCE_ROD.get())
                .add(Items.COPPER_INGOT);
        this.tag(TagRegistry.Items.ECLIPTRUM_PARTS)
                .add(ItemRegistry.TRANSFORMATIVE_ROD.get())
                .add(ItemRegistry.REFLECTIVE_PANEL.get())
                .add(ItemRegistry.ECLIPTRUM_INGOT.get())
                .add(ItemRegistry.ECLIPTRUM_COG.get())
                .add(ItemRegistry.ECLIPTRUM_COG_ASSEMBLY_2X.get())
                .add(ItemRegistry.ECLIPTRUM_COG_ASSEMBLY_4X.get());

        this.tag(TagRegistry.Items.SHAPED_POLISHED_OBSIDIAN_BLOCKS)
                .add(BlockRegistry.POLISHED_OBSIDIAN_COLUMN.get().asItem())
                .add(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN.get().asItem())
                .add(BlockRegistry.POLISHED_OBSIDIAN_BRICKS.get().asItem())
                .add(BlockRegistry.POLISHED_OBSIDIAN_TRACT.get().asItem())
                .add(BlockRegistry.POLISHED_OBSIDIAN_TILES.get().asItem());

        this.tag(TagRegistry.Items.POLISHED_OBSIDIAN_BLOCKS)
                .addTag(TagRegistry.Items.SHAPED_POLISHED_OBSIDIAN_BLOCKS)
                .add(BlockRegistry.POLISHED_OBSIDIAN.get().asItem());

        this.tag(TagRegistry.Items.MAKES_WHITE_DYE)
                .add(Items.LILY_OF_THE_VALLEY)
                .add(Items.BONE_MEAL);
        this.tag(TagRegistry.Items.MAKES_LIGHT_GRAY_DYE)
                .add(Items.AZURE_BLUET)
                .add(Items.WHITE_TULIP)
                .add(Items.OXEYE_DAISY);
        this.tag(TagRegistry.Items.MAKES_GRAY_DYE);
                // vanilla has nothing that directly makes gray???
        this.tag(TagRegistry.Items.MAKES_BLACK_DYE)
                .add(Items.INK_SAC)
                .add(Items.WITHER_ROSE)
                .add(Items.CHARCOAL);
        this.tag(TagRegistry.Items.MAKES_BROWN_DYE)
                .add(Items.COCOA_BEANS)
                .add(Items.BROWN_MUSHROOM);
        this.tag(TagRegistry.Items.MAKES_RED_DYE)
                .add(Items.POPPY)
                .add(Items.RED_TULIP)
                .add(Items.ROSE_BUSH)
                .add(Items.BEETROOT)
                .add(Items.RED_MUSHROOM)
                .add(Items.CRIMSON_FUNGUS);
        this.tag(TagRegistry.Items.MAKES_ORANGE_DYE)
                .add(Items.ORANGE_TULIP)
                .add(Items.TORCHFLOWER);
        this.tag(TagRegistry.Items.MAKES_YELLOW_DYE)
                .add(Items.SUNFLOWER)
                .add(Items.DANDELION);
        this.tag(TagRegistry.Items.MAKES_LIME_DYE);
                // nothing for lime either!
        this.tag(TagRegistry.Items.MAKES_GREEN_DYE)
                .add(Items.CACTUS)
                .add(Items.SHORT_GRASS)
                .add(Items.TALL_GRASS)
                .add(Items.FERN)
                .add(Items.LARGE_FERN);
        this.tag(TagRegistry.Items.MAKES_CYAN_DYE)
                .add(Items.PITCHER_PLANT)
                .add(Items.WARPED_FUNGUS);
        this.tag(TagRegistry.Items.MAKES_LIGHT_BLUE_DYE)
                .add(Items.BLUE_ORCHID);
        this.tag(TagRegistry.Items.MAKES_BLUE_DYE)
                .add(Items.LAPIS_LAZULI)
                .add(Items.CORNFLOWER);
        this.tag(TagRegistry.Items.MAKES_PURPLE_DYE);
                // again, nothing
        this.tag(TagRegistry.Items.MAKES_MAGENTA_DYE)
                .add(Items.ALLIUM)
                .add(Items.LILAC)
                .add(Items.SPORE_BLOSSOM);
        this.tag(TagRegistry.Items.MAKES_PINK_DYE)
                .add(Items.PEONY)
                .add(Items.PINK_PETALS)
                .add(Items.PINK_TULIP);

        this.tag(TagRegistry.Items.HIDDEN_RESOURCES)
                .add(ItemRegistry.LENSING_CRYSTAL.get())
                .add(ItemRegistry.BONDING_POWDER.get())
                .add(ItemRegistry.FROZEN_MOONLIGHT_CHUNK.get());

        this.tag(Tags.Items.ORES)
                .add(BlockRegistry.LENSING_CRYSTAL_ORE.get().asItem());
        this.tag(Tags.Items.ORES_IN_GROUND_STONE)
                .add(BlockRegistry.LENSING_CRYSTAL_ORE.get().asItem());
        this.tag(Tags.Items.GEMS)
                .add(ItemRegistry.LENSING_CRYSTAL.get());
        this.tag(Tags.Items.GLASS_BLOCKS)
                .add(BlockRegistry.SPIRE_GLASS.get().asItem());
        this.tag(Tags.Items.MUSIC_DISCS)
                .add(ItemRegistry.UNDER_THE_SKY_MUSIC_DISC.get());
    }
}