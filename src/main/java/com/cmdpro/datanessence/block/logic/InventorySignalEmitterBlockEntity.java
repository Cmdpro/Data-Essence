package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.api.signal.IStructuralSignalProvider;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventorySignalEmitterBlockEntity extends BlockEntity
        implements IStructuralSignalProvider {

    public InventorySignalEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.INVENTORY_SIGNAL_EMITTER.get(), pos, state);
    }

    @Override
    public Map<String, Integer> getSignals() {
        Map<String, Integer> signals = new HashMap<>();
        Level lvl = this.level;
        if (lvl == null) {
            return signals;
        }

        // 🔹 Look at all adjacent blocks with inventories
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = lvl.getBlockEntity(neighborPos);

            if (neighbor instanceof Container container) {
                int size = container.getContainerSize();
                for (int slot = 0; slot < size; slot++) {
                    ItemStack stack = container.getItem(slot);
                    if (stack.isEmpty()) continue;

                    var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    String signalId = "item:" + itemId.toString(); // e.g. item:minecraft:iron_ingot
                    int count = stack.getCount();

                    signals.merge(signalId, count, Integer::sum);
                }
            }
        }

        return signals;
    }

    // No persistent state right now, but here for future-proofing
    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }
}
