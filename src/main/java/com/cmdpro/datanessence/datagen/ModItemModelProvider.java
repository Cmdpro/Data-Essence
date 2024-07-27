package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class ModItemModelProvider extends ItemModelProvider {
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DataNEssence.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ItemRegistry.DATA_TABLET);
        simpleItem(ItemRegistry.DATA_DRIVE);
        simpleItem(ItemRegistry.ESSENCE_SHARD);
        simpleItemWithSubdirectory(ItemRegistry.ESSENCE_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.LUNAR_ESSENCE_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.NATURAL_ESSENCE_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.EXOTIC_ESSENCE_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.ITEM_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.FLUID_WIRE, "wires");

        handheldItem(ItemRegistry.MAGIC_WRENCH);

        evenSimplerBlockItem(BlockRegistry.FABRICATOR);
        evenSimplerBlockItem(BlockRegistry.ESSENCE_POINT);
        evenSimplerBlockItem(BlockRegistry.LUNAR_ESSENCE_POINT);
        evenSimplerBlockItem(BlockRegistry.NATURAL_ESSENCE_POINT);
        evenSimplerBlockItem(BlockRegistry.EXOTIC_ESSENCE_POINT);
        evenSimplerBlockItem(BlockRegistry.ITEM_POINT);
        evenSimplerBlockItem(BlockRegistry.FLUID_POINT);
        evenSimplerBlockItem(BlockRegistry.ESSENCE_CRYSTAL);
        evenSimplerBlockItem(BlockRegistry.INFUSER);
        evenSimplerBlockItem(BlockRegistry.CHARGER);
        evenSimplerBlockItem(BlockRegistry.ESSENCE_LEECH);

        evenSimplerBlockItem(BlockRegistry.DECO_ESSENCE_BUFFER);
        evenSimplerBlockItem(BlockRegistry.DECO_ITEM_BUFFER);
        evenSimplerBlockItem(BlockRegistry.DECO_FLUID_BUFFER);

        evenSimplerBlockItem(BlockRegistry.ANCIENT_ROCK_COLUMN);
        evenSimplerBlockItem(BlockRegistry.ANCIENT_ROCK_BRICKS);
        evenSimplerBlockItem(BlockRegistry.ANCIENT_ROCK_TILES);
        evenSimplerBlockItem(BlockRegistry.ENERGIZED_ANCIENT_ROCK_COLUMN);
        evenSimplerBlockItem(BlockRegistry.ANCIENT_LANTERN);

        evenSimplerBlockItem(BlockRegistry.POLISHED_OBSIDIAN_COLUMN);

        simpleItem(ItemRegistry.CONDUCTANCE_ROD);
        simpleItem(ItemRegistry.CAPACITANCE_PANEL);
        simpleItem(ItemRegistry.LOGICAL_MATRIX);

        simpleItemWithSubdirectory(ItemRegistry.ESSENCE_BOMB, "bombs");
        simpleItemWithSubdirectory(ItemRegistry.LUNAR_ESSENCE_BOMB, "bombs");
        simpleItemWithSubdirectory(ItemRegistry.NATURAL_ESSENCE_BOMB, "bombs");
        simpleItemWithSubdirectory(ItemRegistry.EXOTIC_ESSENCE_BOMB, "bombs");

    }
    private ItemModelBuilder simpleItem(Supplier<Item> item) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(DataNEssence.MOD_ID,"item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }
    private ItemModelBuilder simpleItemWithSubdirectory(Supplier<Item> item, String subdirectory) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(DataNEssence.MOD_ID,"item/" + subdirectory + "/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }
    private ItemModelBuilder flatBlockItemWithTexture(Supplier<Block> item, ResourceLocation texture) {
        return withExistingParent(BuiltInRegistries.BLOCK.getKey(item.get()).getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                texture);
    }

    public void evenSimplerBlockItem(Supplier<Block> block) {
        this.withExistingParent(DataNEssence.MOD_ID + ":" + BuiltInRegistries.BLOCK.getKey(block.get()).getPath(),
                modLoc("block/" + BuiltInRegistries.BLOCK.getKey(block.get()).getPath()));
    }
    public void wallItem(Supplier<Block> block, Supplier<Block> baseBlock) {
        this.withExistingParent(BuiltInRegistries.BLOCK.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  new ResourceLocation(DataNEssence.MOD_ID, "block/" + BuiltInRegistries.BLOCK.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder handheldItem(Supplier<Item> item) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(DataNEssence.MOD_ID,"item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItem(Supplier<Block> item) {
        return withExistingParent(BuiltInRegistries.BLOCK.getKey(item.get()).getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(DataNEssence.MOD_ID,"item/" + BuiltInRegistries.BLOCK.getKey(item.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItemBlockTexture(Supplier<Block> item) {
        return withExistingParent(BuiltInRegistries.BLOCK.getKey(item.get()).getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(DataNEssence.MOD_ID,"block/" + BuiltInRegistries.BLOCK.getKey(item.get()).getPath()));
    }
}
