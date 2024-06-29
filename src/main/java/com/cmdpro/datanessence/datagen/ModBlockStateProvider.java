package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.EssenceBurner;
import com.cmdpro.datanessence.registry.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

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

        bufferDecoBlock(BlockRegistry.DECO_ESSENCE_BUFFER);
        bufferDecoBlock(BlockRegistry.DECO_ITEM_BUFFER);
        bufferDecoBlock(BlockRegistry.DECO_FLUID_BUFFER);

        decoBlock(BlockRegistry.POLISHED_OBSIDIAN);
        pillarDecoBlock(BlockRegistry.POLISHED_OBSIDIAN_COLUMN);
        decoBlock(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN);
        decoBlock(BlockRegistry.PATTERNED_COPPER);

        essenceBurner(BlockRegistry.ESSENCE_BURNER);

        blockWithItem(BlockRegistry.TRAVERSITE_ROAD);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
    private void cubeColumn(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation loc = blockRegistryObject.getKey().location();
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeColumn(loc.getPath(), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "horizontal"), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath())));
    }
    private void ancientDecoBlockWithItem(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation loc = blockRegistryObject.getKey().location();
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeAll(loc.getPath(), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath())));
    }
    private void ancientDecoCubeColumn(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation loc = blockRegistryObject.getKey().location();
        axisBlock((RotatedPillarBlock)blockRegistryObject.get(), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath()), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/ancient/" + loc.getPath() + "_vertical"));
    }
    private void bufferBlock(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation loc = blockRegistryObject.getKey().location();
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeColumn(loc.getPath(), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath() + "_side"), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath() + "_top_bottom")));
    }
    private void bufferDecoBlock(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation loc = blockRegistryObject.getKey().location();
        axisBlock((RotatedPillarBlock)blockRegistryObject.get(), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath().replaceFirst("deco_", "") + "_side"), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath().replaceFirst("deco_", "") + "_top_bottom"));
    }
    private void decoBlock(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation loc = blockRegistryObject.getKey().location();
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeAll(loc.getPath(), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath())));
    }
    private void pillarDecoBlock(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation loc = blockRegistryObject.getKey().location();
        axisBlock((RotatedPillarBlock)blockRegistryObject.get(), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath() + "_side"), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/" + loc.getPath() + "_vertical"));
    }
    private void essenceBurner(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation loc = blockRegistryObject.getKey().location();
        BlockModelBuilder model = models().withExistingParent(loc.getPath(), ModelProvider.BLOCK_FOLDER + "/cube")
                .texture("west", new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("east", new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_side"))
                .texture("north", new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"))
                .texture("down", new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/deco/polished_obsidian"))
                .texture("up", new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_top"))
                .texture("south", new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_back"))
                .texture("particle", new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + "_front"));
        simpleBlockItem(blockRegistryObject.get(), model);
        getVariantBuilder(blockRegistryObject.get())
                .partialState().with(EssenceBurner.FACING, Direction.EAST).modelForState().modelFile(model).rotationY(90).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.NORTH).modelForState().modelFile(model).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.SOUTH).modelForState().modelFile(model).rotationY(180).addModel()
                .partialState().with(EssenceBurner.FACING, Direction.WEST).modelForState().modelFile(model).rotationY(270).addModel();
    }
}