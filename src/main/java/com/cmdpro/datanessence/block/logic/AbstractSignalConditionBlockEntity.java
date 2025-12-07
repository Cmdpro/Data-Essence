package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.api.signal.IStructuralSignalReceiver;
import com.cmdpro.datanessence.registry.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSignalConditionBlockEntity extends BlockEntity
        implements IStructuralSignalReceiver, MenuProvider {

    public static final int ID_MODE_SPECIFIC = 0;
    public static final int ID_MODE_ANY      = 1; // ∃
    public static final int ID_MODE_ALL      = 2; // ∀

    public static final int COMP_LESS    = -1; // <
    public static final int COMP_EQUAL   = 0;  // =
    public static final int COMP_GREATER = 1;  // >

    private int idMode = ID_MODE_SPECIFIC;
    private int idChar = 'X';
    private int comparison = COMP_EQUAL;
    private int threshold = 1;

    protected final Map<String, Integer> activeSignals = new HashMap<>();
    private long lastSignalsTick = -1L;

    private boolean conditionActive = false;
    private boolean conditionDirty = true;

    protected AbstractSignalConditionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void acceptSignals(Map<String, Integer> signals) {
        Level lvl = this.level;
        if (lvl == null || lvl.isClientSide) return;

        long tick = lvl.getGameTime();
        activeSignals.clear();
        activeSignals.putAll(signals);
        lastSignalsTick = tick;
        conditionDirty = true;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractSignalConditionBlockEntity be) {
        if (level.isClientSide) return;

        long tick = level.getGameTime();

        if (tick != be.lastSignalsTick) {
            if (!be.activeSignals.isEmpty()) {
                be.activeSignals.clear();
                be.conditionDirty = true;
            }
            be.lastSignalsTick = tick;
        }

        if (be.conditionDirty) {
            boolean newActive = be.evaluateCondition();
            if (newActive != be.conditionActive) {
                be.conditionActive = newActive;
                be.onConditionUpdated(newActive);
                be.setChanged();
                BlockState st = level.getBlockState(pos);
                level.sendBlockUpdated(pos, st, st, 3);
            }
            be.conditionDirty = false;
        }
    }

    private boolean evaluateCondition() {
        if (activeSignals.isEmpty()) {
            if (idMode == ID_MODE_ANY || idMode == ID_MODE_ALL) {
                return false;
            }
        }

        return switch (idMode) {
            case ID_MODE_SPECIFIC -> {
                String target = String.valueOf((char) idChar);
                int value = activeSignals.getOrDefault(target, 0);
                yield compare(value, threshold);
            }
            case ID_MODE_ANY -> {
                for (int v : activeSignals.values()) {
                    if (compare(v, threshold)) {
                        yield true;
                    }
                }
                yield false;
            }
            case ID_MODE_ALL -> {
                if (activeSignals.isEmpty()) {
                    yield false;
                }
                for (int v : activeSignals.values()) {
                    if (!compare(v, threshold)) {
                        yield false;
                    }
                }
                yield true;
            }
            default -> false;
        };
    }

    private boolean compare(int value, int threshold) {
        return switch (comparison) {
            case COMP_LESS -> value < threshold;
            case COMP_EQUAL -> value == threshold;
            case COMP_GREATER -> value > threshold;
            default -> false;
        };
    }

    protected abstract void onConditionUpdated(boolean active);

    public int getIdMode() {
        return idMode;
    }

    public int getIdChar() {
        return idChar;
    }

    public int getComparison() {
        return comparison;
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean isConditionActive() {
        return conditionActive;
    }

    public void setIdMode(int idMode) {
        this.idMode = idMode;
        markConfigChanged();
    }

    public void setIdChar(int idChar) {
        this.idChar = idChar;
        markConfigChanged();
    }

    public void setComparison(int comparison) {
        this.comparison = comparison;
        markConfigChanged();
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
        markConfigChanged();
    }

    private void markConfigChanged() {
        Level lvl = this.level;
        if (lvl != null && !lvl.isClientSide) {
            conditionDirty = true;
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("IdMode", idMode);
        tag.putInt("IdChar", idChar);
        tag.putInt("Comparison", comparison);
        tag.putInt("Threshold", threshold);
        tag.putBoolean("Active", conditionActive);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("IdMode")) idMode = tag.getInt("IdMode");
        if (tag.contains("IdChar")) idChar = tag.getInt("IdChar");
        if (tag.contains("Comparison")) comparison = tag.getInt("Comparison");
        if (tag.contains("Threshold")) threshold = tag.getInt("Threshold");
        if (tag.contains("Active")) conditionActive = tag.getBoolean("Active");
        conditionDirty = true;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.datanessence.signal_condition");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new SignalConditionMenu(id, inv, this);
    }
}
