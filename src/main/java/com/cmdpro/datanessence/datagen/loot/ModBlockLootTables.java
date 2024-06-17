package com.cmdpro.datanessence.datagen.loot;

import com.cmdpro.datanessence.init.BlockInit;
import com.cmdpro.datanessence.init.ItemInit;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(BlockInit.FABRICATOR.get());
        dropSelf(BlockInit.ESSENCE_POINT.get());
        dropSelf(BlockInit.LUNAR_ESSENCE_POINT.get());
        dropSelf(BlockInit.NATURAL_ESSENCE_POINT.get());
        dropSelf(BlockInit.EXOTIC_ESSENCE_POINT.get());
        dropSelf(BlockInit.ESSENCE_BUFFER.get());
        dropSelf(BlockInit.ITEM_BUFFER.get());
        dropSelf(BlockInit.FLUID_BUFFER.get());
        dropSelf(BlockInit.ITEM_POINT.get());
        dropSelf(BlockInit.FLUID_POINT.get());
        dropSelf(BlockInit.ESSENCE_CRYSTAL.get());
        dropSelf(BlockInit.ESSENCE_BURNER.get());
        dropSelf(BlockInit.DECO_ESSENCE_BUFFER.get());
        dropSelf(BlockInit.DECO_ITEM_BUFFER.get());
        dropSelf(BlockInit.DECO_FLUID_BUFFER.get());
        this.add(BlockInit.DATA_BANK.get(),
                block -> noDrop());
        this.add(BlockInit.ESSENCE_CRYSTAL.get(), block -> createEssenceCrystalDrops(block, ItemInit.ESSENCE_SHARD.get()));
        dropSelf(BlockInit.ANCIENT_ROCK_COLUMN.get());
        dropSelf(BlockInit.ANCIENT_ROCK_BRICKS.get());
        dropSelf(BlockInit.ANCIENT_ROCK_TILES.get());
        dropSelf(BlockInit.ENERGIZED_ANCIENT_ROCK_COLUMN.get());
    }
    protected LootTable.Builder createEssenceCrystalDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get).filter((block) -> {
            if (block instanceof LiquidBlock) {
                return false;
            }
            return true;
        })::iterator;
    }
}