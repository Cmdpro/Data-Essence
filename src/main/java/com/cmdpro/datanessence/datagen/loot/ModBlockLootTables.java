package com.cmdpro.datanessence.datagen.loot;

import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;
import java.util.function.Supplier;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        dropSelf(BlockRegistry.FABRICATOR.get());
        dropSelf(BlockRegistry.ESSENCE_POINT.get());
        dropSelf(BlockRegistry.LUNAR_ESSENCE_POINT.get());
        dropSelf(BlockRegistry.NATURAL_ESSENCE_POINT.get());
        dropSelf(BlockRegistry.EXOTIC_ESSENCE_POINT.get());
        dropSelf(BlockRegistry.ESSENCE_BUFFER.get());
        dropSelf(BlockRegistry.ITEM_BUFFER.get());
        dropSelf(BlockRegistry.FLUID_BUFFER.get());
        dropSelf(BlockRegistry.ITEM_POINT.get());
        dropSelf(BlockRegistry.FLUID_POINT.get());
        dropSelf(BlockRegistry.ESSENCE_CRYSTAL.get());
        dropSelf(BlockRegistry.ESSENCE_BURNER.get());
        dropSelf(BlockRegistry.DECO_ESSENCE_BUFFER.get());
        dropSelf(BlockRegistry.DECO_ITEM_BUFFER.get());
        dropSelf(BlockRegistry.DECO_FLUID_BUFFER.get());
        this.add(BlockRegistry.DATA_BANK.get(),
                block -> noDrop());
        this.add(BlockRegistry.COMPUTER.get(),
                block -> noDrop());
        this.add(BlockRegistry.ESSENCE_CRYSTAL.get(), block -> createEssenceCrystalDrops(block, ItemRegistry.ESSENCE_SHARD.get()));
        dropSelf(BlockRegistry.ANCIENT_ROCK_COLUMN.get());
        dropSelf(BlockRegistry.ANCIENT_ROCK_BRICKS.get());
        dropSelf(BlockRegistry.ANCIENT_ROCK_TILES.get());
        dropSelf(BlockRegistry.ENERGIZED_ANCIENT_ROCK_COLUMN.get());
        dropSelf(BlockRegistry.ANCIENT_LANTERN.get());
        dropSelf(BlockRegistry.POLISHED_OBSIDIAN.get());
        dropSelf(BlockRegistry.POLISHED_OBSIDIAN_COLUMN.get());
        dropSelf(BlockRegistry.ENGRAVED_POLISHED_OBSIDIAN.get());
        dropSelf(BlockRegistry.TRAVERSITE_ROAD.get());
        dropSelf(BlockRegistry.ESSENCE_BATTERY.get());
        dropSelf(BlockRegistry.LUNAR_ESSENCE_BATTERY.get());
        dropSelf(BlockRegistry.NATURAL_ESSENCE_BATTERY.get());
        dropSelf(BlockRegistry.EXOTIC_ESSENCE_BATTERY.get());
        dropSelf(BlockRegistry.INFUSER.get());
        dropSelf(BlockRegistry.PATTERNED_COPPER.get());
        dropSelf(BlockRegistry.FLUID_COLLECTOR.get());
        dropSelf(BlockRegistry.FLUID_SPILLER.get());
        dropSelf(BlockRegistry.CHARGER.get());
        dropSelf(BlockRegistry.LASER_EMITTER.get());
        dropSelf(BlockRegistry.VACUUM.get());
        this.add(BlockRegistry.STRUCTURE_PROTECTOR.get(),
                block -> noDrop());
        dropSelf(BlockRegistry.ESSENCE_LEECH.get());
        this.add(BlockRegistry.LENSING_CRYSTAL_ORE.get(), block -> createLensingCrystalDrops(block, ItemRegistry.LENSING_CRYSTAL.get()));
        dropSelf(BlockRegistry.AUTO_FABRICATOR.get());
        dropSelf(BlockRegistry.FLUID_TANK.get());
        dropSelf(BlockRegistry.FLUID_MIXER.get());
        dropSelf(BlockRegistry.SYNTHESIS_CHAMBER.get());
        dropSelf(BlockRegistry.FLUID_BOTTLER.get());
        dropSelf(BlockRegistry.ENTROPIC_PROCESSOR.get());
        dropSelf(BlockRegistry.ESSENCE_FURNACE.get());
        dropSelf(BlockRegistry.ENTICING_LURE.get());
        dropSelf(BlockRegistry.FLUIDIC_GLASS.get());
        dropSelf(BlockRegistry.AETHER_RUNE.get());
        dropSelf(BlockRegistry.MINERAL_PURIFICATION_CHAMBER.get());
        dropSelf(BlockRegistry.ITEM_FILTER.get());
        dropSelf(BlockRegistry.ESSENCE_BREAKER.get());
        dropSelf(BlockRegistry.LIMITED_ITEM_BUFFER.get());
    }
    protected LootTable.Builder createEssenceCrystalDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE)))));
    }
    protected LootTable.Builder createLensingCrystalDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE)))));
    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BlockRegistry.BLOCKS.getEntries().stream().map((block) -> (Block)block.get()).filter((block) -> {
            if (block instanceof LiquidBlock) {
                return false;
            }
            return true;
        })::iterator;
    }
}