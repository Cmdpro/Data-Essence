package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.init.BlockInit;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DataNEssence.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        bufferBlock(BlockInit.ESSENCE_BUFFER);
        bufferBlock(BlockInit.ITEM_BUFFER);
        bufferBlock(BlockInit.FLUID_BUFFER);

        ancientDecoBlockWithItem(BlockInit.ANCIENT_ROCK_BRICKS);
        ancientDecoBlockWithItem(BlockInit.ANCIENT_ROCK_TILES);
        ancientDecoCubeColumn(BlockInit.ANCIENT_ROCK_COLUMN);
        ancientDecoCubeColumn(BlockInit.ENERGIZED_ANCIENT_ROCK_COLUMN);

        bufferDecoBlock(BlockInit.DECO_ESSENCE_BUFFER);
        bufferDecoBlock(BlockInit.DECO_ITEM_BUFFER);
        bufferDecoBlock(BlockInit.DECO_FLUID_BUFFER);
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
}