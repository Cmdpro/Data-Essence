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

        bufferBlock(BlockInit.ESSENCEBUFFER);
        bufferBlock(BlockInit.ITEMBUFFER);
        bufferBlock(BlockInit.FLUIDBUFFER);

        bufferDecoBlock(BlockInit.DECOESSENCEBUFFER);
        bufferDecoBlock(BlockInit.DECOITEMBUFFER);
        bufferDecoBlock(BlockInit.DECOFLUIDBUFFER);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
    private void bufferBlock(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation loc = blockRegistryObject.getKey().location();
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeColumn(loc.getPath(), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath() + "side"), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath() + "topbottom")));
    }
    private void bufferDecoBlock(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation loc = blockRegistryObject.getKey().location();
        axisBlock((RotatedPillarBlock)blockRegistryObject.get(), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath().replaceFirst("deco", "") + "side"), new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/buffer/" + loc.getPath().replaceFirst("deco", "") + "topbottom"));
    }
}