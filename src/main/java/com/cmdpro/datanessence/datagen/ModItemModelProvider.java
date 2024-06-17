package com.cmdpro.datanessence.datagen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.init.BlockInit;
import com.cmdpro.datanessence.init.ItemInit;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;

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
        simpleItem(ItemInit.DATATABLET);
        simpleItem(ItemInit.DATADRIVE);
        simpleItem(ItemInit.ESSENCESHARD);
        simpleItemWithSubdirectory(ItemInit.ESSENCEWIRE, "wires");
        simpleItemWithSubdirectory(ItemInit.LUNARESSENCEWIRE, "wires");
        simpleItemWithSubdirectory(ItemInit.NATURALESSENCEWIRE, "wires");
        simpleItemWithSubdirectory(ItemInit.EXOTICESSENCEWIRE, "wires");
        simpleItemWithSubdirectory(ItemInit.ITEMWIRE, "wires");
        simpleItemWithSubdirectory(ItemInit.FLUIDWIRE, "wires");

        handheldItem(ItemInit.MAGICWRENCH);

        evenSimplerBlockItem(BlockInit.FABRICATOR);
        evenSimplerBlockItem(BlockInit.ESSENCEPOINT);
        evenSimplerBlockItem(BlockInit.LUNARESSENCEPOINT);
        evenSimplerBlockItem(BlockInit.NATURALESSENCEPOINT);
        evenSimplerBlockItem(BlockInit.EXOTICESSENCEPOINT);
        evenSimplerBlockItem(BlockInit.ITEMPOINT);
        evenSimplerBlockItem(BlockInit.FLUIDPOINT);
        evenSimplerBlockItem(BlockInit.ESSENCECRYSTAL);

        evenSimplerBlockItem(BlockInit.DECOESSENCEBUFFER);
        evenSimplerBlockItem(BlockInit.DECOITEMBUFFER);
        evenSimplerBlockItem(BlockInit.DECOFLUIDBUFFER);

        evenSimplerBlockItem(BlockInit.ANCIENTROCKCOLUMN);
        evenSimplerBlockItem(BlockInit.ANCIENTROCKBRICKS);
        evenSimplerBlockItem(BlockInit.ANCIENTROCKTILES);
        evenSimplerBlockItem(BlockInit.ENERGIZEDANCIENTROCKCOLUMN);

    }
    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(DataNEssence.MOD_ID,"item/" + item.getId().getPath()));
    }
    private ItemModelBuilder simpleItemWithSubdirectory(RegistryObject<Item> item, String subdirectory) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(DataNEssence.MOD_ID,"item/" + subdirectory + "/" + item.getId().getPath()));
    }
    private ItemModelBuilder flatBlockItemWithTexture(RegistryObject<Block> item, ResourceLocation texture) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                texture);
    }

    public void evenSimplerBlockItem(RegistryObject<Block> block) {
        this.withExistingParent(DataNEssence.MOD_ID + ":" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }
    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  new ResourceLocation(DataNEssence.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder handheldItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(DataNEssence.MOD_ID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(DataNEssence.MOD_ID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItemBlockTexture(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(DataNEssence.MOD_ID,"block/" + item.getId().getPath()));
    }
}
