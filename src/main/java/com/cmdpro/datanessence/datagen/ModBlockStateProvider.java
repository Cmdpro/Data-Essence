package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.generation.EssenceBurner;
import com.cmdpro.datanessence.block.production.FluidCollector;
import com.cmdpro.datanessence.block.auxiliary.LaserEmitter;
import com.cmdpro.datanessence.registry.BlockRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DataNEssence.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        bufferBlock(BlockRegistry.ESSENCE_BUFFER);
        bufferBlock(BlockRegistry.ITEM_BUFFER);
        bufferBlock(BlockRegistry.FLUID_BUFFER);

        ancientDecoBlockWithItem(BlockRegistry.ANCIENT_ROCK_BRICKS);
        ancientDecoBlockWithItem(BlockRegistry.ANCIENT_ROCK_TILES);
        ancientDecoCubeColumn(BlockRegistry.ANCIENT_ROCK_COLUMN);
        ancientDecoCubeColumn(BlockRegistry.ENERGIZED_ANCIENT_ROCK_COLUMN);
        ancientDecoBlockWithItem(BlockRegistry.ANCIENT_LANTERN);

        bufferDecoBlock(BlockRegistry.DECO_ESSENCE_BUFFER);
        bufferDecoBlock(BlockRegistry.DECO_ITEM_BUFFER);
        bufferDecoBlock(BlockRegistry.DECO_FLUID_BUFFER);

        decoBlock(BlockRegistry.POLISHED_OBSIDIAN);
        pillarDecoBlock(BlockRegistry.POLISHED_OBSIDIAN_COLUMN);
        decoBlock(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN);
        decoBlock(BlockRegistry.PATTERNED_COPPER);
        decoBlock(BlockRegistry.FLUIDIC_GLASS);
        decoBlock(BlockRegistry.AETHER_RUNE);

        essenceBurner(BlockRegistry.ESSENCE_BURNER);
        dataBank(BlockRegistry.DATA_BANK);
        fluidCollector(BlockRegistry.FLUID_COLLECTOR);
        fluidCollector(BlockRegistry.FLUID_SPILLER);
        laserEmitter(BlockRegistry.LASER_EMITTER);
        essenceBattery(BlockRegistry.ESSENCE_BATTERY);
        itemFilter(BlockRegistry.ITEM_FILTER);

        nothing(BlockRegistry.STRUCTURE_PROTECTOR);

        blockWithItem(BlockRegistry.TRAVERSITE_ROAD);
        //essenceLeech(BlockRegistry.ESSENCE_LEECH);
        //blockWithItem(BlockRegistry.LENSING_CRYSTAL_ORE);

        transparentBlockWithItem(BlockRegistry.SPIRE_GLASS);
    }

    private void blockWithItem(Supplier<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void transparentBlockWithItem(Supplier<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeAll(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("translucent"));
    }

    private void cubeColumn(Supplier<Block> blockRegistryObject) {
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get());
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeColumn(loc.getPath(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath())));
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
        axisBlock((RotatedPillarBlock)blockRegistryObject.get(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath()), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath() + "_vertical"));
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
        axisBlock((RotatedPillarBlock)blockRegistryObject.get(), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath() + "_side"), ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath() + "_vertical"));
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
        simpleBlockItem(blockRegistryObject.get(), model);
        getVariantBuilder(blockRegistryObject.get())
                .partialState().with(EssenceBurner.FACING, Direction.EAST).modelForState().modelFile(model).rotationY(90).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.NORTH).modelForState().modelFile(model).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.SOUTH).modelForState().modelFile(model).rotationY(180).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.WEST).modelForState().modelFile(model).rotationY(270).addModel();
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
        simpleBlockWithItem(blockRegistryObject.get(), model);
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
}