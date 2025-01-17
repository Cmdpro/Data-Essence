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
        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, name));
        }
    }
    public static class Items {
        public static final TagKey<Item> LOW_ESSENCE_PLANTS = tag("low_essence_plants");
        public static final TagKey<Item> MEDIUM_ESSENCE_PLANTS = tag("medium_essence_plants");
        public static final TagKey<Item> HIGH_ESSENCE_PLANTS = tag("high_essence_plants");
        public static final TagKey<Item> POLISHED_OBSIDIAN_BLOCKS = tag("polished_obsidian_blocks");
        public static final TagKey<Item> SHAPED_POLISHED_OBSIDIAN_BLOCKS = tag("shaped_polished_obsidian_blocks");
        public static final TagKey<Item> EXPLOSIVE_MATERIAL = tag("explosive_material");
        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, name));
        }
    }
    public static class EntityTypes {
        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, name));
        }
    }
}
