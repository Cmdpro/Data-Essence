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
        dropSelf(BlockInit.ESSENCEPOINT.get());
        dropSelf(BlockInit.LUNARESSENCEPOINT.get());
        dropSelf(BlockInit.NATURALESSENCEPOINT.get());
        dropSelf(BlockInit.EXOTICESSENCEPOINT.get());
        dropSelf(BlockInit.ESSENCEBUFFER.get());
        dropSelf(BlockInit.ITEMBUFFER.get());
        dropSelf(BlockInit.FLUIDBUFFER.get());
        dropSelf(BlockInit.ITEMPOINT.get());
        dropSelf(BlockInit.FLUIDPOINT.get());
        dropSelf(BlockInit.ESSENCECRYSTAL.get());
        this.add(BlockInit.DATABANK.get(),
                block -> noDrop());
        this.add(BlockInit.ESSENCECRYSTAL.get(), block -> createEssenceCrystalDrops(block, ItemInit.ESSENCESHARD.get()));
    }
    protected LootTable.Builder createEssenceCrystalDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(8.0F, 12.0F)))
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