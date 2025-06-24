package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagRegistry {
    public static class Blocks {
        public static final TagKey<Block> BUFFER_DETECTION_PASS = tag("buffer_detection_pass");
        public static final TagKey<Block> HAMMER_AND_CHISEL_COLLECTABLE = tag("hammer_and_chisel_collectable"); // for the Hammer and Chisel tool
        public static final TagKey<Block> CRYSTALLINE_CRADLE_BREAKABLE = tag("crystalline_cradle_breakable");
        public static final TagKey<Block> SCANNABLE_ORES = tag("scannable_ores");
        public static final TagKey<Block> PEARLESCENT_SAND = tag("pearlescent_sand");
        public static final TagKey<Block> SANCTUARY_SAND = tag("sanctuary_sand");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(DataNEssence.locate(name));
        }
    }
    public static class Items {
        // Industrial Plant Siphon fuels
        public static final TagKey<Item> LOW_ESSENCE_PLANTS = tag("low_essence_plants");
        public static final TagKey<Item> MEDIUM_ESSENCE_PLANTS = tag("medium_essence_plants");
        public static final TagKey<Item> HIGH_ESSENCE_PLANTS = tag("high_essence_plants");

        // Block types
        public static final TagKey<Item> POLISHED_OBSIDIAN_BLOCKS = tag("polished_obsidian_blocks");
        public static final TagKey<Item> SHAPED_POLISHED_OBSIDIAN_BLOCKS = tag("shaped_polished_obsidian_blocks");

        // Crafting materials
        public static final TagKey<Item> EXPLOSIVE_MATERIAL = tag("explosive_material");

        // Repair materials
        public static final TagKey<Item> COPPER_PARTS = tag("copper_parts");
        public static final TagKey<Item> ECLIPTRUM_PARTS = tag("ecliptrum_parts");

        // Dye materials
        public static final TagKey<Item> MAKES_WHITE_DYE = tag("makes_white_dye");
        public static final TagKey<Item> MAKES_LIGHT_GRAY_DYE = tag("makes_light_gray_dye");
        public static final TagKey<Item> MAKES_GRAY_DYE = tag("makes_gray_dye");
        public static final TagKey<Item> MAKES_BLACK_DYE = tag("makes_black_dye");
        public static final TagKey<Item> MAKES_BROWN_DYE = tag("makes_brown_dye");
        public static final TagKey<Item> MAKES_RED_DYE = tag("makes_red_dye");
        public static final TagKey<Item> MAKES_ORANGE_DYE = tag("makes_orange_dye");
        public static final TagKey<Item> MAKES_YELLOW_DYE = tag("makes_yellow_dye");
        public static final TagKey<Item> MAKES_LIME_DYE = tag("makes_lime_dye");
        public static final TagKey<Item> MAKES_GREEN_DYE = tag("makes_green_dye");
        public static final TagKey<Item> MAKES_CYAN_DYE = tag("makes_cyan_dye");
        public static final TagKey<Item> MAKES_LIGHT_BLUE_DYE = tag("makes_light_blue_dye");
        public static final TagKey<Item> MAKES_BLUE_DYE = tag("makes_blue_dye");
        public static final TagKey<Item> MAKES_PURPLE_DYE = tag("makes_purple_dye");
        public static final TagKey<Item> MAKES_MAGENTA_DYE = tag("makes_magenta_dye");
        public static final TagKey<Item> MAKES_PINK_DYE = tag("makes_pink_dye");

        // Advancement trigger materials
        public static final TagKey<Item> HIDDEN_RESOURCES = tag ("hidden_resources");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(DataNEssence.locate(name));
        }
    }
    public static class EntityTypes {
        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, DataNEssence.locate(name));
        }
    }
}
