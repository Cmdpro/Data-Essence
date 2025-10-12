package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.FluidRegistry;
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
        simpleItem(ItemRegistry.COGNIZANT_CUBE);
        simpleItem(ItemRegistry.UNDER_THE_SKY_MUSIC_DISC);
        simpleItem(ItemRegistry.ESSENCE_SHARD);
        simpleItem(ItemRegistry.LUNAR_ESSENCE_SHARD);
        simpleItem(ItemRegistry.PRIMITIVE_ANTI_GRAVITY_PACK);
        simpleItem(ItemRegistry.TRAVERSITE_TRUDGERS);
        simpleItem(ItemRegistry.TRAVERSITE_ROAD_CHUNK);
        simpleItem(ItemRegistry.WARP_CAPSULE);
        simpleItem(ItemRegistry.THERMOMETER);
        simpleItem(ItemRegistry.MOLD_ROD);
        simpleItem(ItemRegistry.MOLD_PANEL);
        simpleItem(ItemRegistry.HAMMER_AND_CHISEL);
        simpleItem(ItemRegistry.LOCATOR);
        simpleItem(ItemRegistry.ECLIPTRUM_INGOT);
        simpleItem(ItemRegistry.ESSENCE_METER);
        simpleItem(ItemRegistry.ORE_SCANNER);

        simpleItemWithSubdirectory(ItemRegistry.ESSENCE_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.LUNAR_ESSENCE_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.NATURAL_ESSENCE_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.EXOTIC_ESSENCE_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.ITEM_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.FLUID_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.CHEMICAL_WIRE, "wires");
        simpleItemWithSubdirectory(ItemRegistry.RF_WIRE, "wires");

        simpleItemWithSubdirectory(ItemRegistry.ACCELERATION_LENS, "lenses");
        simpleItemWithSubdirectory(ItemRegistry.BURNING_LENS, "lenses");
        simpleItemWithSubdirectory(ItemRegistry.HARMING_LENS, "lenses");
        simpleItemWithSubdirectory(ItemRegistry.HEALING_LENS, "lenses");
        simpleItemWithSubdirectory(ItemRegistry.PRECISION_LENS, "lenses");
        simpleItemWithSubdirectory(ItemRegistry.ATTRACTING_LENS, "lenses");

        simpleItemWithSubdirectory(ItemRegistry.SPRITE_BOOK_FLORA, "sprites");

        handheldItem(ItemRegistry.ESSENCE_REDIRECTOR);
        handheldItem(ItemRegistry.ESSENCE_SWORD);
        handheldItem(ItemRegistry.ILLUMINATION_ROD);
        handheldItem(ItemRegistry.REPULSION_ROD);
        handheldItem(ItemRegistry.ENERGY_RIFLE);

        evenSimplerBlockItem(BlockRegistry.ESSENCE_CRYSTAL);
        evenSimplerBlockItem(BlockRegistry.LUNAR_ESSENCE_CRYSTAL);

        evenSimplerBlockItem(BlockRegistry.DECO_ESSENCE_BUFFER);
        evenSimplerBlockItem(BlockRegistry.DECO_ITEM_BUFFER);
        evenSimplerBlockItem(BlockRegistry.DECO_FLUID_BUFFER);

        evenSimplerBlockItem(BlockRegistry.ANCIENT_ROCK_COLUMN);
        evenSimplerBlockItem(BlockRegistry.ANCIENT_ROCK_BRICKS);
        evenSimplerBlockItem(BlockRegistry.ANCIENT_ROCK_TILES);
        evenSimplerBlockItem(BlockRegistry.ENERGIZED_ANCIENT_ROCK_COLUMN);
        evenSimplerBlockItem(BlockRegistry.ANCIENT_LANTERN);
        evenSimplerBlockItem(BlockRegistry.SPIRE_GLASS);
        evenSimplerBlockItem(BlockRegistry.CRYSTALLINE_LEAVES);
        evenSimplerBlockItem(BlockRegistry.CRYSTALLINE_LOG);

        evenSimplerBlockItem(BlockRegistry.POLISHED_OBSIDIAN_COLUMN);
        evenSimplerBlockItem(BlockRegistry.POLISHED_OBSIDIAN_TRACT);

        evenSimplerBlockItem(BlockRegistry.TRAVERSITE_ROAD_STAIRS);
        evenSimplerBlockItem(BlockRegistry.TRAVERSITE_ROAD_SLAB);
        evenSimplerBlockItem(BlockRegistry.TRAVERSITE_ROAD_STAIRS_OPAL);
        evenSimplerBlockItem(BlockRegistry.TRAVERSITE_ROAD_SLAB_OPAL);

        simpleItemWithSubdirectory(ItemRegistry.CONDUCTANCE_ROD, "components");
        simpleItemWithSubdirectory(ItemRegistry.CAPACITANCE_PANEL, "components");
        simpleItemWithSubdirectory(ItemRegistry.LOGICAL_MATRIX, "components");
        simpleItemWithSubdirectory(ItemRegistry.LENS, "components");
        simpleItemWithSubdirectory(ItemRegistry.PROPELLER, "components");
        simpleItemWithSubdirectory(ItemRegistry.WIRE_SPOOL, "components");
        simpleItemWithSubdirectory(ItemRegistry.IRON_DRILL, "components");
        simpleItemWithSubdirectory(ItemRegistry.EXCITER, "components");
        simpleItemWithSubdirectory(ItemRegistry.HEATING_COIL, "components");
        simpleItemWithSubdirectory(ItemRegistry.COPPER_SHELL, "components");
        simpleItemWithSubdirectory(ItemRegistry.TRANSFORMATIVE_ROD, "components");
        simpleItemWithSubdirectory(ItemRegistry.REFLECTIVE_PANEL, "components");
        simpleItemWithSubdirectory(ItemRegistry.ECLIPTRUM_COG, "components");
        simpleItemWithSubdirectory(ItemRegistry.ECLIPTRUM_COG_ASSEMBLY_2X, "components");
        simpleItemWithSubdirectory(ItemRegistry.ECLIPTRUM_COG_ASSEMBLY_4X, "components");
        simpleItemWithSubdirectory(ItemRegistry.EXTRICATION_ROD, "components");

        simpleItemWithSubdirectory(ItemRegistry.COPPER_NUGGET, "intermediates");
        simpleItemWithSubdirectory(ItemRegistry.DIAMOND_SHARD, "intermediates");
        simpleItemWithSubdirectory(ItemRegistry.EMERALD_SHARD, "intermediates");
        simpleItemWithSubdirectory(ItemRegistry.COAL_LUMP, "intermediates");

        simpleItemWithSubdirectory(ItemRegistry.ESSENCE_BOMB, "bombs");
        simpleItemWithSubdirectory(ItemRegistry.LUNAR_ESSENCE_BOMB, "bombs");
        simpleItemWithSubdirectory(ItemRegistry.NATURAL_ESSENCE_BOMB, "bombs");
        simpleItemWithSubdirectory(ItemRegistry.EXOTIC_ESSENCE_BOMB, "bombs");

        simpleItemWithSubdirectory(ItemRegistry.SPEED_UPGRADE, "sigils");
        simpleItemWithSubdirectory(ItemRegistry.FILTER_UPGRADE, "sigils");

        simpleItem(ItemRegistry.LENSING_CRYSTAL);
        simpleItem(ItemRegistry.BONDING_POWDER);
        simpleItem(FluidRegistry.GENDERFLUID.bucket::get);
        simpleItem(ItemRegistry.FROZEN_MOONLIGHT_CHUNK);
        simpleItem(ItemRegistry.TRANSFORMATIVE_GEL);

        essencePoint(BlockRegistry.LUNAR_ESSENCE_POINT);
        essencePoint(BlockRegistry.NATURAL_ESSENCE_POINT);
        essencePoint(BlockRegistry.EXOTIC_ESSENCE_POINT);
        essencePoint(BlockRegistry.ITEM_POINT);
        essencePoint(BlockRegistry.FLUID_POINT);
        essencePoint(BlockRegistry.CHEMICAL_NODE);
        essencePoint(BlockRegistry.RF_NODE);

        musicDiscPlayer(ItemRegistry.MUSIC_DISC_PLAYER);
        grapplingHook(ItemRegistry.GRAPPLING_HOOK);
        grapplingHook(ItemRegistry.TRANS_GRAPPLING_HOOK);

    }
    private ItemModelBuilder musicDiscPlayer(Supplier<Item> item) {
        withExistingParent("music_disc_player_on", ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0", ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"item/music_disc_player_on"));
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                        ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"item/music_disc_player"))
                .override().predicate(DataNEssence.locate("playing"), 1).
                model(getExistingFile(DataNEssence.locate("music_disc_player_on"))).end();
    }
    private ItemModelBuilder simpleItem(Supplier<Item> item) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }
    private ItemModelBuilder grapplingHook(Supplier<Item> item) {
        ItemModelBuilder using = withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath() + "_use",
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath() + "_use"));

        ItemModelBuilder charged = withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath() + "_charged",
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                        ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));

        ItemModelBuilder empty = withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                        ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath() + "_empty"))
                .override().predicate(DataNEssence.locate("charged"), 1).predicate(DataNEssence.locate("using"), 0).model(charged).end()
                .override().predicate(DataNEssence.locate("charged"), 1).predicate(DataNEssence.locate("using"), 1).model(using).end();
        return empty;
    }
    private ItemModelBuilder simpleItemWithSubdirectory(Supplier<Item> item, String subdirectory) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"item/" + subdirectory + "/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }
    private ItemModelBuilder flatBlockItemWithTexture(Supplier<Block> item, ResourceLocation texture) {
        return withExistingParent(BuiltInRegistries.BLOCK.getKey(item.get()).getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                texture);
    }

    public void evenSimplerBlockItem(Supplier<Block> block) {
        this.withExistingParent(DataNEssence.MOD_ID + ":" + BuiltInRegistries.BLOCK.getKey(block.get()).getPath(),
                modLoc("block/" + BuiltInRegistries.BLOCK.getKey(block.get()).getPath()));
    }
    public void essencePoint(Supplier<Block> item) {
        this.withExistingParent(DataNEssence.MOD_ID + ":" + BuiltInRegistries.ITEM.getKey(item.get().asItem()).getPath(),
                modLoc("item/essence_point"));
    }
    public void wallItem(Supplier<Block> block, Supplier<Block> baseBlock) {
        this.withExistingParent(BuiltInRegistries.BLOCK.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  DataNEssence.locate("block/" + BuiltInRegistries.BLOCK.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder handheldItem(Supplier<Item> item) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(),
                ResourceLocation.withDefaultNamespace("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItem(Supplier<Block> item) {
        return withExistingParent(BuiltInRegistries.BLOCK.getKey(item.get()).getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"item/" + BuiltInRegistries.BLOCK.getKey(item.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItemBlockTexture(Supplier<Block> item) {
        return withExistingParent(BuiltInRegistries.BLOCK.getKey(item.get()).getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID,"block/" + BuiltInRegistries.BLOCK.getKey(item.get()).getPath()));
    }
}
