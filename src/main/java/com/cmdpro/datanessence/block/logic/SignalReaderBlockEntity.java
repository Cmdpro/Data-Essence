package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.api.signal.IStructuralSignalProvider;
import com.cmdpro.datanessence.api.signal.IStructuralSignalReceiver;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads structural signals from attached structural nodes,
 * shows them (via GUI/renderer) AND re-outputs the *current* signals.
 */
public class SignalReaderBlockEntity extends BlockEntity
        implements IStructuralSignalReceiver, IStructuralSignalProvider {

    // All signals received on the latest tick we got anything
    private final Map<String, Integer> currentSignals = new HashMap<>();

    // Server tick when currentSignals were last updated
    private long lastUpdateTick = -1L;

    public SignalReaderBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SIGNAL_READER.get(), pos, state);
    }

    //  IStructuralSignalReceiver: called by StructuralPointBlockEntity.transfer

    @Override
    public void acceptSignals(Map<String, Integer> signals) {
        Level lvl = this.level;
        if (lvl == null || lvl.isClientSide) return;

        long tick = lvl.getGameTime();

        // First bundle this tick? reset the map so we only have "current" values.
        if (lastUpdateTick != tick) {
            currentSignals.clear();
        }

        // Merge all incoming signals for this tick
        for (Map.Entry<String, Integer> entry : signals.entrySet()) {
            String id = entry.getKey();
            int amount = entry.getValue();
            if (amount <= 0) continue;

            currentSignals.merge(id, amount, Integer::sum);
        }

        lastUpdateTick = tick;

        setChanged();
        BlockState state = lvl.getBlockState(worldPosition);
        lvl.sendBlockUpdated(worldPosition, state, state, 3);
    }

    //  IStructuralSignalProvider: what the reader outputs to structural wires

    /**
     * Returns ONLY signals that were updated in the *current* tick.
     * If no signals arrived this tick, returns empty -> no output.
     */
    @Override
    public Map<String, Integer> getSignals() {
        Level lvl = this.level;
        if (lvl == null) {
            return Collections.emptyMap();
        }

        long tick = lvl.getGameTime();
        if (tick != lastUpdateTick || currentSignals.isEmpty()) {
            // No fresh signals on this tick -> nothing to output
            return Collections.emptyMap();
        }

        // Return an immutable copy to avoid external mutation
        return Map.copyOf(currentSignals);
    }

    // Optional legacy single-signal API, if anything still uses it
    @Override
    public String getSignalId() {
        Map<String, Integer> signals = getSignals();
        if (signals.isEmpty()) return null;
        // if you need a "primary" one, just pick the first
        return signals.keySet().iterator().next();
    }

    @Override
    public int getSignalAmount() {
        String id = getSignalId();
        if (id == null) return 0;
        return currentSignals.getOrDefault(id, 0);
    }

    //  For GUI / display: last known signals (even if not current)

    /** For screens/renderer: shows the last received signals even if the network is idle. */
    public Map<String, Integer> getDisplaySignals() {
        return Map.copyOf(currentSignals);
    }

    //  NBT – persist last known signals (useful so GUI isn’t empty on reload)

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        ListTag list = new ListTag();
        for (Map.Entry<String, Integer> entry : currentSignals.entrySet()) {
            CompoundTag s = new CompoundTag();
            s.putString("Id", entry.getKey());
            s.putInt("Amount", entry.getValue());
            list.add(s);
        }
        tag.put("Signals", list);
        tag.putLong("LastUpdateTick", lastUpdateTick);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        currentSignals.clear();

        if (tag.contains("Signals", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Signals", Tag.TAG_COMPOUND);
            for (Tag t : list) {
                CompoundTag s = (CompoundTag) t;
                String id = s.getString("Id");
                int amount = s.getInt("Amount");
                currentSignals.put(id, amount);
            }
        }

        lastUpdateTick = tag.getLong("LastUpdateTick");
    }
}
