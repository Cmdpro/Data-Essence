package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.DirectionalPillarBlock;
import com.cmdpro.datanessence.block.generation.EssenceBurner;
import com.cmdpro.datanessence.block.production.FluidCollector;
import com.cmdpro.datanessence.block.auxiliary.LaserEmitter;
import com.cmdpro.datanessence.registry.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DataNEssence.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        bufferBlock(BlockRegistry.ESSENCE_BUFFER);
        bufferBlock(BlockRegistry.ITEM_BUFFER);
        bufferBlock(BlockRegistry.LIMITED_ITEM_BUFFER);
        bufferBlock(BlockRegistry.FLUID_BUFFER);

        ancientDecoBlockWithItem(BlockRegistry.ANCIENT_ROCK_BRICKS);
        ancientDecoBlockWithItem(BlockRegistry.ANCIENT_ROCK_TILES);
        ancientDecoCubeColumn(BlockRegistry.ANCIENT_ROCK_COLUMN);
        ancientDecoCubeColumn(BlockRegistry.ENERGIZED_ANCIENT_ROCK_COLUMN);
        ancientDecoBlockWithItem(BlockRegistry.ANCIENT_LANTERN);
        ancientShelf(BlockRegistry.ANCIENT_SHELF);
        transparentAncientDecoBlockWithItem(BlockRegistry.ANCIENT_WINDOW);
        ancientDecoBlockWithItem(BlockRegistry.ANCIENT_GLYPH_STONE_BLANK);
        ancientDecoBlockWithItem(BlockRegistry.ANCIENT_GLYPH_STONE_MAKUTUIN);
        ancientDecoBlockWithItem(BlockRegistry.ANCIENT_GLYPH_STONE_ESSENCE);

        parentedBlockWithItem(BlockRegistry.SHIELDLESS_ANCIENT_ROCK_BRICKS, DataNEssence.locate("ancient_rock_bricks"));
        parentedBlockWithItem(BlockRegistry.SHIELDLESS_ANCIENT_ROCK_TILES, DataNEssence.locate("ancient_rock_tiles"));
        parentedBlockWithItem(BlockRegistry.SHIELDLESS_ANCIENT_ROCK_COLUMN, DataNEssence.locate("ancient_rock_column"));
        parentedBlockWithItem(BlockRegistry.SHIELDLESS_ENERGIZED_ANCIENT_ROCK_COLUMN, DataNEssence.locate("energized_ancient_rock_column"));
        parentedBlockWithItem(BlockRegistry.SHIELDLESS_ANCIENT_LANTERN, DataNEssence.locate("ancient_lantern"));
        //parentedBlockWithItem(BlockRegistry.SHIELDLESS_ANCIENT_SHELF, DataNEssence.locate("ancient_shelf")); // does not gen properly, multi-model block. done manually for now
        parentedBlockWithItem(BlockRegistry.SHIELDLESS_ANCIENT_WINDOW, DataNEssence.locate("ancient_window"));
        parentedBlockWithItem(BlockRegistry.SHIELDLESS_ANCIENT_GLYPH_STONE_BLANK, DataNEssence.locate("ancient_glyph_stone_blank"));
        parentedBlockWithItem(BlockRegistry.SHIELDLESS_ANCIENT_GLYPH_STONE_ESSENCE, DataNEssence.locate("ancient_glyph_stone_essence"));
        parentedBlockWithItem(BlockRegistry.SHIELDLESS_ANCIENT_GLYPH_STONE_MAKUTUIN, DataNEssence.locate("ancient_glyph_stone_makutuin"));

        bufferDecoBlock(BlockRegistry.DECO_ESSENCE_BUFFER);
        bufferDecoBlock(BlockRegistry.DECO_ITEM_BUFFER);
        bufferDecoBlock(BlockRegistry.DECO_FLUID_BUFFER);

        decoBlock(BlockRegistry.POLISHED_OBSIDIAN);
        decoBlock(BlockRegistry.POLISHED_OBSIDIAN_BRICKS);
        decoBlock(BlockRegistry.POLISHED_OBSIDIAN_TILES);
        pillarDecoBlock(BlockRegistry.POLISHED_OBSIDIAN_COLUMN);
        pillarDecoBlock(BlockRegistry.POLISHED_OBSIDIAN_TRACT);
        decoBlock(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN);
        decoBlock(BlockRegistry.PATTERNED_COPPER);
        transparentDecoBlockWithItem(BlockRegistry.FLUIDIC_GLASS);
        transparentDecoBlockWithItem(BlockRegistry.OBSIDIAN_FRAMED_GLASS);
        decoBlock(BlockRegistry.AETHER_RUNE);

        essenceBurner(BlockRegistry.ESSENCE_BURNER);
        dataBank(BlockRegistry.ANCIENT_DATA_BANK);
        fluidCollector(BlockRegistry.FLUID_COLLECTOR);
        fluidCollector(BlockRegistry.FLUID_SPILLER);
        laserEmitter(BlockRegistry.LASER_EMITTER);
        essenceBattery(BlockRegistry.ESSENCE_BATTERY);
        itemFilter(BlockRegistry.ITEM_FILTER);
        crystallineLog(BlockRegistry.CRYSTALLINE_LOG);
        essenceLeech(BlockRegistry.ESSENCE_LEECH);
        essenceFurnace(BlockRegistry.ESSENCE_FURNACE);
        mineralPurificationChamber(BlockRegistry.MINERAL_PURIFICATION_CHAMBER);
        synthesisChamber(BlockRegistry.SYNTHESIS_CHAMBER);

        nothing(BlockRegistry.STRUCTURE_PROTECTOR);

        blockWithItem(BlockRegistry.TRAVERSITE_ROAD);
        blockWithItem(BlockRegistry.LENSING_CRYSTAL_ORE);
        blockWithItem(BlockRegistry.CREATIVE_ESSENCE_BATTERY);

        transparentBlockWithItemAndTint(BlockRegistry.SPIRE_GLASS);

        stairsBlock((StairBlock)BlockRegistry.TRAVERSITE_ROAD_STAIRS.get(), DataNEssence.locate("block/traversite_road"));
        slabBlock((SlabBlock) BlockRegistry.TRAVERSITE_ROAD_SLAB.get(), DataNEssence.locate("block/traversite_road"), DataNEssence.locate("block/traversite_road"));

        blockWithItemTintedOverlay(BlockRegistry.TRAVERSITE_ROAD_OPAL, ResourceLocation.fromNamespaceAndPath("opalescence", "block/opal"), DataNEssence.locate("block/traversite_road_opal_overlay"), DataNEssence.locate("block/traversite_road"));
        stairsBlockTintedOverlay((StairBlock)BlockRegistry.TRAVERSITE_ROAD_STAIRS_OPAL.get(), ResourceLocation.fromNamespaceAndPath("opalescence", "block/opal"), DataNEssence.locate("block/traversite_road_opal_overlay"), DataNEssence.locate("block/traversite_road"));
        slabBlockTintedOverlay((SlabBlock) BlockRegistry.TRAVERSITE_ROAD_SLAB_OPAL.get(), DataNEssence.locate("block/traversite_road_opal"), ResourceLocation.fromNamespaceAndPath("opalescence", "block/opal"), DataNEssence.locate("block/traversite_road_opal_overlay"), DataNEssence.locate("block/traversite_road"));
    }
    public void slabBlockTintedOverlay(SlabBlock block, ResourceLocation doubleSlab, ResourceLocation texture, ResourceLocation overlay, ResourceLocation particle) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(block);
        ModelFile slab = models().withExistingParent(loc.getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/slab_tinted_overlay"))
                .texture("side", texture)
                .texture("bottom", texture)
                .texture("top", texture)
                .texture("sideoverlay", overlay)
                .texture("bottomoverlay", overlay)
                .texture("topoverlay", overlay)
                .texture("particle", particle);
        ModelFile slabTop = models().withExistingParent(loc.getPath() + "_top", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/slab_top_tinted_overlay"))
                .texture("side", texture)
                .texture("bottom", texture)
                .texture("top", texture)
                .texture("sideoverlay", overlay)
                .texture("bottomoverlay", overlay)
                .texture("topoverlay", overlay)
                .texture("particle", particle);
        slabBlock(block, slab, slabTop, models().getExistingFile(doubleSlab));
    }
    private void stairsBlockTintedOverlay(StairBlock block, ResourceLocation texture, ResourceLocation overlay, ResourceLocation particle) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(block);
        ModelFile stairs = models().withExistingParent(loc.getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/stairs_tinted_overlay"))
                .texture("side", texture)
                .texture("bottom", texture)
                .texture("top", texture)
                .texture("sideoverlay", overlay)
                .texture("bottomoverlay", overlay)
                .texture("topoverlay", overlay)
                .texture("particle", particle);
        ModelFile stairsInner = models().withExistingParent(loc.getPath() + "_inner", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/inner_stairs_tinted_overlay"))
                .texture("side", texture)
                .texture("bottom", texture)
                .texture("top", texture)
                .texture("sideoverlay", overlay)
                .texture("bottomoverlay", overlay)
                .texture("topoverlay", overlay)
                .texture("particle", particle);
        ModelFile stairsOuter = models().withExistingParent(loc.getPath() + "_outer", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/outer_stairs_tinted_overlay"))
                .texture("side", texture)
                .texture("bottom", texture)
                .texture("top", texture)
                .texture("sideoverlay", overlay)
                .texture("bottomoverlay", overlay)
                .texture("topoverlay", overlay)
                .texture("particle", particle);
        stairsBlock(block, stairs, stairsInner, stairsOuter);
    }
    private void blockWithItemTintedOverlay(Supplier<Block> blockRegistryObject, ResourceLocation texture, ResourceLocation overlay, ResourceLocation particle) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        ModelFile cube = models().withExistingParent(loc.getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/cube_all_tinted_overlay"))
                .texture("all", texture)
                .texture("overlay", overlay)
                .texture("particle", particle);
        simpleBlockWithItem(blockRegistryObject.get(), cube);
    }

    private void blockWithItem(Supplier<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
    private void blockWithItemWithVariants(Supplier<Block> blockRegistryObject, String... variantPostfixes) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        List<ModelFile> variants = new ArrayList<>();
        for (String i : variantPostfixes) {
            variants.add(models().cubeAll(loc.getPath() + "_" + i, ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_" + i)));
        }
        simpleBlockItem(blockRegistryObject.get(), variants.getFirst());
        simpleBlock(blockRegistryObject.get(), variants.stream().map(ConfiguredModel::new).toList().toArray(new ConfiguredModel[0]));
    }
    private void ancientDecoBlockWithItemWithVariants(Supplier<Block> blockRegistryObject, String... variantPostfixes) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        List<ModelFile> variants = new ArrayList<>();
        for (String i : variantPostfixes) {
            variants.add(models().cubeAll(loc.getPath() + "_" + i, ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath() + "_" + i)));
        }
        simpleBlockItem(blockRegistryObject.get(), variants.getFirst());
        simpleBlock(blockRegistryObject.get(), variants.stream().map(ConfiguredModel::new).toList().toArray(new ConfiguredModel[0]));
    }
    private void ancientShelf(Supplier<Block> blockRegistryObject) {
        String[] variantPostfixes = new String[] {
                "books", "books2", "bottles", "empty", "empty2", "materials"
        };
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        List<ModelFile> variants = new ArrayList<>();
        for (String i : variantPostfixes) {
            ResourceLocation side = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath() + "_" + i);
            ResourceLocation vertical = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/ancient_rock_column_vertical");
            variants.add(models().cube(loc.getPath() + "_" + i, vertical, vertical, side, side, side, side).texture("particle", vertical));
        }
        simpleBlockItem(blockRegistryObject.get(), variants.getFirst());
        simpleBlock(blockRegistryObject.get(), variants.stream().map(ConfiguredModel::new).toList().toArray(new ConfiguredModel[0]));
    }

    private void transparentBlockWithItem(Supplier<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeAll(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("translucent"));
    }
    private void transparentBlockWithItemAndTint(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlockWithItem(blockRegistryObject.get(), models().singleTexture(loc.getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/cube_all_tinted"), "all", blockTexture(blockRegistryObject.get())).renderType("translucent"));
    }
    private void transparentDecoBlockWithItem(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeAll(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath())).renderType("translucent"));
    }
    private void transparentAncientDecoBlockWithItem(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeAll(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath())).renderType("translucent"));
    }

    private void cubeColumn(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeColumn(loc.getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath())));
    }
    private void crystallineLog(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        axisBlock((RotatedPillarBlock)blockRegistryObject.get(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath()), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath() + "_top"));
    }
    private void essenceBattery(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeColumn(loc.getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_end")));
    }
    private void ancientDecoBlockWithItem(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeAll(loc.getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath())));
    }
    private void ancientDecoCubeColumn(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        ResourceLocation side = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath() + "_side");
        ResourceLocation vertical = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath() + "_vertical");
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", side)
                .texture("east", side)
                .texture("north", side)
                .texture("down", vertical)
                .texture("up", vertical)
                .texture("south", side)
                .texture("particle", side);
        simpleBlockItem(blockRegistryObject.get(), model);
        getVariantBuilder(blockRegistryObject.get())
                .partialState().with(DirectionalPillarBlock.FACING, Direction.EAST).modelForState().modelFile(model).rotationY(90).rotationX(90).addModel()
                .partialState().with(DirectionalPillarBlock.FACING, Direction.NORTH).modelForState().modelFile(model).rotationX(90).addModel()
                .partialState().with(DirectionalPillarBlock.FACING, Direction.SOUTH).modelForState().modelFile(model).rotationY(180).rotationX(90).addModel()
                .partialState().with(DirectionalPillarBlock.FACING, Direction.WEST).modelForState().modelFile(model).rotationY(270).rotationX(90).addModel()
                .partialState().with(DirectionalPillarBlock.FACING, Direction.UP).modelForState().modelFile(model).addModel()
                .partialState().with(DirectionalPillarBlock.FACING, Direction.DOWN).modelForState().modelFile(model).rotationX(180).addModel();
    }
    private void bufferBlock(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeColumn(loc.getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath() + "_side"), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath() + "_top_bottom")));
    }
    private void bufferDecoBlock(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        axisBlock((RotatedPillarBlock)blockRegistryObject.get(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath().replaceFirst("deco_", "") + "_side"), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath().replaceFirst("deco_", "") + "_top_bottom"));
    }
    private void decoBlock(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeAll(loc.getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath())));
    }
    private void pillarDecoBlock(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        ResourceLocation side = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath() + "_side");
        ResourceLocation vertical = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath() + "_vertical");
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", side)
                .texture("east", side)
                .texture("north", side)
                .texture("down", vertical)
                .texture("up", vertical)
                .texture("south", side)
                .texture("particle", side);
        simpleBlockItem(blockRegistryObject.get(), model);
        getVariantBuilder(blockRegistryObject.get())
                .partialState().with(DirectionalPillarBlock.FACING, Direction.EAST).modelForState().modelFile(model).rotationY(90).rotationX(90).addModel()
                .partialState().with(DirectionalPillarBlock.FACING, Direction.NORTH).modelForState().modelFile(model).rotationX(90).addModel()
                .partialState().with(DirectionalPillarBlock.FACING, Direction.SOUTH).modelForState().modelFile(model).rotationY(180).rotationX(90).addModel()
                .partialState().with(DirectionalPillarBlock.FACING, Direction.WEST).modelForState().modelFile(model).rotationY(270).rotationX(90).addModel()
                .partialState().with(DirectionalPillarBlock.FACING, Direction.UP).modelForState().modelFile(model).addModel()
                .partialState().with(DirectionalPillarBlock.FACING, Direction.DOWN).modelForState().modelFile(model).rotationX(180).addModel();
    }
    private void essenceLeech(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("east", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("north", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("down", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_bottom"))
                .texture("up", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top"))
                .texture("south", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"));
        simpleBlockWithItem(blockRegistryObject.get(), model);
    }
    private void essenceBurner(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("east", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("north", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"))
                .texture("down", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/polished_obsidian"))
                .texture("up", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top"))
                .texture("south", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_back"))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"));
        BlockModelBuilder modelLit = models().withExistingParent(loc.getPath() + "_lit", ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("east", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("north", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front_on"))
                .texture("down", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/polished_obsidian"))
                .texture("up", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top_on"))
                .texture("south", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_back"))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front_on"));
        simpleBlockItem(blockRegistryObject.get(), model);
        getVariantBuilder(blockRegistryObject.get())
                .partialState().with(EssenceBurner.FACING, Direction.EAST).with(EssenceBurner.LIT, false).modelForState().modelFile(model).rotationY(90).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.NORTH).with(EssenceBurner.LIT, false).modelForState().modelFile(model).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.SOUTH).with(EssenceBurner.LIT, false).modelForState().modelFile(model).rotationY(180).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.WEST).with(EssenceBurner.LIT, false).modelForState().modelFile(model).rotationY(270).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.EAST).with(EssenceBurner.LIT, true).modelForState().modelFile(modelLit).rotationY(90).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.NORTH).with(EssenceBurner.LIT, true).modelForState().modelFile(modelLit).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.SOUTH).with(EssenceBurner.LIT, true).modelForState().modelFile(modelLit).rotationY(180).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.WEST).with(EssenceBurner.LIT, true).modelForState().modelFile(modelLit).rotationY(270).addModel();
    }

    private void mineralPurificationChamber(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("east", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("north", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"))
                .texture("down", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_bottom"))
                .texture("up", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top"))
                .texture("south", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"));
        simpleBlockItem(blockRegistryObject.get(), model);
        getVariantBuilder(blockRegistryObject.get())
                .partialState().with(EssenceBurner.FACING, Direction.EAST).modelForState().modelFile(model).rotationY(90).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.NORTH).modelForState().modelFile(model).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.SOUTH).modelForState().modelFile(model).rotationY(180).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.WEST).modelForState().modelFile(model).rotationY(270).addModel();
    }

    private void synthesisChamber(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("east", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("north", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"))
                .texture("down", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/polished_obsidian"))
                .texture("up", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top"))
                .texture("south", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"));
        simpleBlockItem(blockRegistryObject.get(), model);
        getVariantBuilder(blockRegistryObject.get())
                .partialState().with(EssenceBurner.FACING, Direction.EAST).modelForState().modelFile(model).rotationY(90).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.NORTH).modelForState().modelFile(model).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.SOUTH).modelForState().modelFile(model).rotationY(180).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.WEST).modelForState().modelFile(model).rotationY(270).addModel();
    }

    private void essenceFurnace(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("east", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("north", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"))
                .texture("down", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/patterned_copper"))
                .texture("up", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top"))
                .texture("south", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"));
        BlockModelBuilder modelLit = models().withExistingParent(loc.getPath() + "_lit", ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("east", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("north", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front_on"))
                .texture("down", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/patterned_copper"))
                .texture("up", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top"))
                .texture("south", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front_on"));
        simpleBlockItem(blockRegistryObject.get(), model);
        getVariantBuilder(blockRegistryObject.get())
                .partialState().with(EssenceBurner.FACING, Direction.EAST).with(EssenceBurner.LIT, false).modelForState().modelFile(model).rotationY(90).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.NORTH).with(EssenceBurner.LIT, false).modelForState().modelFile(model).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.SOUTH).with(EssenceBurner.LIT, false).modelForState().modelFile(model).rotationY(180).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.WEST).with(EssenceBurner.LIT, false).modelForState().modelFile(model).rotationY(270).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.EAST).with(EssenceBurner.LIT, true).modelForState().modelFile(modelLit).rotationY(90).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.NORTH).with(EssenceBurner.LIT, true).modelForState().modelFile(modelLit).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.SOUTH).with(EssenceBurner.LIT, true).modelForState().modelFile(modelLit).rotationY(180).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.WEST).with(EssenceBurner.LIT, true).modelForState().modelFile(modelLit).rotationY(270).addModel();
    }
    private void itemFilter(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_west"))
                .texture("east", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_east"))
                .texture("north", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_north"))
                .texture("down", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_bottom"))
                .texture("up", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top"))
                .texture("south", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_south"))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top"));
        simpleBlockWithItem(blockRegistryObject.get(), model);
    }
    private void dataBank(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/ancient_rock_bricks"))
                .texture("east", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/ancient_rock_bricks"))
                .texture("north", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"))
                .texture("down", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/ancient_rock_bricks"))
                .texture("up", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/ancient_rock_bricks"))
                .texture("south", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/ancient_rock_bricks"))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"));
        simpleBlockItem(blockRegistryObject.get(), model);
        getVariantBuilder(blockRegistryObject.get())
                .partialState().with(EssenceBurner.FACING, Direction.EAST).modelForState().modelFile(model).rotationY(90).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.NORTH).modelForState().modelFile(model).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.SOUTH).modelForState().modelFile(model).rotationY(180).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.WEST).modelForState().modelFile(model).rotationY(270).addModel();
    }
    private void fluidCollector(Supplier<Block> blockRegistryObject) {
        VariantBlockStateBuilder builder = getVariantBuilder(blockRegistryObject.get());
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        ResourceLocation side = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side");
        ResourceLocation bottom = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_bottom");
        ResourceLocation top = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top");
        ResourceLocation pump = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_pump");
        ResourceLocation pumpTop = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_pump_top");
        ResourceLocation pumpBottom = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_pump_bottom");
        for (Direction i : Direction.values()) {
            BlockModelBuilder model = models().withExistingParent(loc.getPath() + "_" + i.getName().toLowerCase(), ModelProvider.BLOCK_FOLDER + "/cube")
                    .texture("west", i == Direction.WEST ? pump : side)
                    .texture("east", i == Direction.EAST ? pump : side)
                    .texture("north", i == Direction.NORTH ? pump : side)
                    .texture("south", i == Direction.SOUTH ? pump : side)
                    .texture("down", i == Direction.DOWN ? pumpBottom : bottom)
                    .texture("up", i == Direction.UP ? pumpTop : top)
                    .texture("particle", side);
            builder.partialState().with(FluidCollector.FACING, i).modelForState().modelFile(model).addModel();
        }
        BlockModelBuilder model = models().withExistingParent(loc.getPath() + "_hand", ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", side)
                .texture("east", side)
                .texture("north", side)
                .texture("south", side)
                .texture("down", pumpBottom)
                .texture("up", top)
                .texture("particle", side);
        simpleBlockItem(blockRegistryObject.get(), model);
    }
    private void laserEmitter(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        ResourceLocation side = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side");
        ResourceLocation back = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_back");
        ResourceLocation front = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front");
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", side)
                .texture("east", side)
                .texture("north", side)
                .texture("down", back)
                .texture("up", front)
                .texture("south", side)
                .texture("particle", side);
        simpleBlockItem(blockRegistryObject.get(), model);
        getVariantBuilder(blockRegistryObject.get())
                .partialState().with(LaserEmitter.FACING, Direction.EAST).modelForState().modelFile(model).rotationY(90).rotationX(90).addModel()
                .partialState().with(LaserEmitter.FACING, Direction.NORTH).modelForState().modelFile(model).rotationX(90).addModel()
                .partialState().with(LaserEmitter.FACING, Direction.SOUTH).modelForState().modelFile(model).rotationY(180).rotationX(90).addModel()
                .partialState().with(LaserEmitter.FACING, Direction.WEST).modelForState().modelFile(model).rotationY(270).rotationX(90).addModel()
                .partialState().with(LaserEmitter.FACING, Direction.UP).modelForState().modelFile(model).addModel()
                .partialState().with(LaserEmitter.FACING, Direction.DOWN).modelForState().modelFile(model).rotationX(180).addModel();
    }
    private void nothing(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        ResourceLocation particle = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + "nothing");
        BlockModelBuilder model = models().getBuilder(loc.getPath())
                .texture("particle", particle);
        simpleBlock(blockRegistryObject.get(), model);
    }

    private void parentedBlock(Supplier<Block> blockRegistryObject, ResourceLocation parent) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlock(blockRegistryObject.get(), models().withExistingParent(loc.getPath(), parent));
    }

    private void parentedBlockWithItem(Supplier<Block> blockRegistryObject, ResourceLocation parent) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlockWithItem(blockRegistryObject.get(), models().withExistingParent(loc.getPath(), parent));
    }
}