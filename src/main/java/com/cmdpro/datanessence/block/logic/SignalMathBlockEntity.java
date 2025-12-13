package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.api.signal.IStructuralSignalProvider;
import com.cmdpro.datanessence.api.signal.IStructuralSignalReceiver;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignalMathBlockEntity extends BlockEntity
        implements IStructuralSignalProvider, IStructuralSignalReceiver, MenuProvider {

    private final Map<String, Integer> inputSignals = new HashMap<>();
    private long lastUpdateTick = -1L;

    // config
    private String targetId = "";    // which signal id to modify; empty = none
    private int operand = 1;         // number to use in math op
    private int operationIndex = 0;  // enum ordinal
    private boolean forEach = false; // if true: apply to all signals

    public SignalMathBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SIGNAL_MATH.get(), pos, state);
    }

    // math ops
    public enum MathOperation {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE,
        POWER,
        SQRT,
        BIT_AND,
        BIT_OR,
        BIT_XOR,
        SHL,
        SHR,
        USHR;

        public static MathOperation fromIndex(int idx) {
            MathOperation[] values = values();
            if (idx < 0 || idx >= values.length) return ADD;
            return values[idx];
        }
    }

    // receive all signals from structural network
    @Override
    public void acceptSignals(Map<String, Integer> signalsIn) {
        Level lvl = this.level;
        if (lvl == null || lvl.isClientSide) return;

        long tick = lvl.getGameTime();
        if (tick != lastUpdateTick) {
            inputSignals.clear();
            lastUpdateTick = tick;
        }

        for (Map.Entry<String, Integer> e : signalsIn.entrySet()) {
            int amount = e.getValue();
            if (amount <= 0) continue;
            inputSignals.merge(e.getKey(), amount, Integer::sum);
        }

        setChangedAndUpdate();
    }

    // provide transformed signals
    @Override
    public Map<String, Integer> getSignals() {
        if (inputSignals.isEmpty()) return Collections.emptyMap();

        Map<String, Integer> result = new HashMap<>();
        MathOperation op = MathOperation.fromIndex(operationIndex);

        for (Map.Entry<String, Integer> e : inputSignals.entrySet()) {
            String id = e.getKey();
            int amt = e.getValue();
            if (amt <= 0) continue;

            boolean apply = forEach || (!targetId.isEmpty() && id.equals(targetId));
            int outAmt = apply ? applyOp(op, amt, operand) : amt;

            if (outAmt > 0) {
                result.put(id, outAmt);
            }
        }

        return result;
    }

    private int applyOp(MathOperation op, int value, int operand) {
        return switch (op) {
            case ADD      -> safeInt((long) value + operand);
            case SUBTRACT -> safeInt((long) value - operand);
            case MULTIPLY -> safeInt((long) value * operand);
            case DIVIDE   -> (operand == 0 ? value : safeInt(value / operand));
            case POWER    -> {
                if (operand < 0) yield 0;
                long r = 1;
                for (int i = 0; i < operand; i++) {
                    r *= value;
                    if (r > Integer.MAX_VALUE) {
                        r = Integer.MAX_VALUE;
                        break;
                    }
                }
                yield (int) r;
            }
            case SQRT     -> (int) Math.floor(Math.sqrt(Math.max(0, value)));

            case BIT_AND  -> (value & operand);
            case BIT_OR   -> (value | operand);
            case BIT_XOR  -> (value ^ operand);
            case SHL      -> (value << operand);
            case SHR      -> (value >> operand);
            case USHR     -> (value >>> operand);
        };
    }

    private int safeInt(long v) {
        if (v < 0) return 0;
        if (v > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) v;
    }

    // config API for menu

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String id) {
        this.targetId = (id == null ? "" : id);
        setChangedAndUpdate();
    }

    public int getOperand() {
        return operand;
    }

    public void setOperand(int operand) {
        this.operand = Math.max(0, operand);
        setChangedAndUpdate();
    }

    public int getOperationIndex() {
        return operationIndex;
    }

    public void setOperationIndex(int operationIndex) {
        this.operationIndex = Mth.clamp(operationIndex, 0, MathOperation.values().length - 1);
        setChangedAndUpdate();
    }

    public boolean isForEach() {
        return forEach;
    }

    public void setForEach(boolean forEach) {
        this.forEach = forEach;
        setChangedAndUpdate();
    }

    private void setChangedAndUpdate() {
        setChanged();
        Level lvl = this.level;
        if (lvl != null && !lvl.isClientSide) {
            BlockState st = lvl.getBlockState(worldPosition);
            lvl.sendBlockUpdated(worldPosition, st, st, 3);
        }
    }

    // saving / syncing

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putLong("LastUpdateTick", lastUpdateTick);

        ListTag list = new ListTag();
        for (Map.Entry<String, Integer> e : inputSignals.entrySet()) {
            CompoundTag s = new CompoundTag();
            s.putString("Id", e.getKey());
            s.putInt("Amount", e.getValue());
            list.add(s);
        }
        tag.put("InputSignals", list);

        tag.putString("TargetId", targetId);
        tag.putInt("Operand", operand);
        tag.putInt("OperationIndex", operationIndex);
        tag.putBoolean("ForEach", forEach);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        inputSignals.clear();
        if (tag.contains("InputSignals", Tag.TAG_LIST)) {
            ListTag list = tag.getList("InputSignals", Tag.TAG_COMPOUND);
            for (Tag t : list) {
                CompoundTag s = (CompoundTag) t;
                String id = s.getString("Id");
                int amount = s.getInt("Amount");
                inputSignals.put(id, amount);
            }
        }

        lastUpdateTick = tag.getLong("LastUpdateTick");
        targetId = tag.getString("TargetId");
        operand = tag.getInt("Operand");
        operationIndex = tag.getInt("OperationIndex");
        forEach = tag.getBoolean("ForEach");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void onDataPacket(Connection connection,
                             ClientboundBlockEntityDataPacket pkt,
                             HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        loadAdditional(tag, registries);
    }

    // menu

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.datanessence.signal_math");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new SignalMathMenu(id, inv, this);
    }
}
