package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.registry.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SignalMathMenu extends AbstractContainerMenu implements ISignalConfigMenu {

    public static final int BTN_OP_BASE        = 200; // 200..(200+ops-1)
    public static final int BTN_TOGGLE_FOREACH = 250; // ∃ / ∀ toggle

    private final BlockPos pos;
    private final Level level;

    // same basic fields as emitter, but here "amount" is the operand
    private int tabIndex;
    private int signalType;
    private int charCode;
    private int operand;
    private int blockIndex;
    private int opIndex;
    private int forEachFlag;

    // server ctor
    public SignalMathMenu(int id, Inventory inv, SignalMathBlockEntity be) {
        super(MenuRegistry.SIGNAL_MATH_MENU.get(), id);
        this.pos = be.getBlockPos();
        this.level = be.getLevel();

        // decode targetId -> char / block
        String targetId = be.getTargetId();
        if (targetId != null && targetId.startsWith("block:")) {
            signalType = SignalEmitterMenu.TYPE_BLOCK;
            tabIndex   = SignalEmitterMenu.TAB_BLOCKS;

            String raw = targetId.substring("block:".length());
            ResourceLocation rl = ResourceLocation.tryParse(raw);
            if (rl != null) {
                int idx = SignalEmitterMenu.getAllBlockIds().indexOf(rl);
                blockIndex = (idx < 0 ? 0 : idx);
            } else {
                blockIndex = 0;
            }
            charCode = 'X';
        } else if (targetId != null && !targetId.isEmpty()) {
            signalType = SignalEmitterMenu.TYPE_CHAR;
            tabIndex   = SignalEmitterMenu.TAB_CHARS;
            charCode   = targetId.charAt(0);
            blockIndex = 0;
        } else {
            signalType = SignalEmitterMenu.TYPE_CHAR;
            tabIndex   = SignalEmitterMenu.TAB_CHARS;
            charCode   = 'X';
            blockIndex = 0;
        }

        operand     = be.getOperand();
        opIndex     = be.getOperationIndex();
        forEachFlag = be.isForEach() ? 1 : 0;

        addDataSlots();
    }

    // client ctor
    public SignalMathMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(MenuRegistry.SIGNAL_MATH_MENU.get(), id);
        this.pos = buf.readBlockPos();
        this.level = inv.player.level();

        tabIndex    = SignalEmitterMenu.TAB_CHARS;
        signalType  = SignalEmitterMenu.TYPE_CHAR;
        charCode    = 'X';
        operand     = 1;
        blockIndex  = 0;
        opIndex     = 0;
        forEachFlag = 0;

        addDataSlots();
    }

    private void addDataSlots() {
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return tabIndex; }
            @Override public void set(int value) { tabIndex = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return signalType; }
            @Override public void set(int value) { signalType = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return charCode; }
            @Override public void set(int value) { charCode = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return operand; }
            @Override public void set(int value) { operand = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockIndex; }
            @Override public void set(int value) { blockIndex = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return opIndex; }
            @Override public void set(int value) { opIndex = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return forEachFlag; }
            @Override public void set(int value) { forEachFlag = value; }
        });
    }

    private SignalMathBlockEntity getMathBE() {
        if (level == null) return null;
        BlockEntity be = level.getBlockEntity(pos);
        return (be instanceof SignalMathBlockEntity m) ? m : null;
    }

    // ISignalConfigMenu implementation (so we can reuse the screen)

    @Override
    public boolean isBlockMode() {
        return tabIndex == SignalEmitterMenu.TAB_BLOCKS;
    }

    @Override
    public boolean isBlockSignal() {
        return signalType == SignalEmitterMenu.TYPE_BLOCK;
    }

    @Override
    public String getSignalId() {
        if (signalType == SignalEmitterMenu.TYPE_BLOCK) {
            int size = SignalEmitterMenu.getAllBlockIds().size();
            if (blockIndex < 0 || blockIndex >= size) return "block:?";
            return "block:" + SignalEmitterMenu.getAllBlockIds().get(blockIndex);
        }
        return String.valueOf((char) charCode);
    }

    @Override
    public int getAmount() {
        return operand; // here amount == operand
    }

    @Override
    public int getBlockIndex() {
        return blockIndex;
    }

    // extra getters

    public int getOperationIndex() {
        return opIndex;
    }

    public boolean isForEach() {
        return forEachFlag != 0;
    }

    // button handling (SERVER side)

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        SignalMathBlockEntity be = getMathBE();
        if (be == null) return false;

        // Tabs
        if (buttonId == SignalEmitterMenu.BTN_TAB_CHARS) {
            tabIndex = SignalEmitterMenu.TAB_CHARS;
            return true;
        }
        if (buttonId == SignalEmitterMenu.BTN_TAB_BLOCKS) {
            tabIndex = SignalEmitterMenu.TAB_BLOCKS;
            return true;
        }

        // digits 0–9: target char '0'..'9'
        if (buttonId >= SignalEmitterMenu.DIGIT_BASE &&
                buttonId <  SignalEmitterMenu.DIGIT_BASE + 10) {
            char c = (char) ('0' + (buttonId - SignalEmitterMenu.DIGIT_BASE));
            signalType = SignalEmitterMenu.TYPE_CHAR;
            charCode   = c;
            be.setTargetId(String.valueOf(c));
            return true;
        }

        // letters A–Z
        if (buttonId >= SignalEmitterMenu.LETTER_BASE &&
                buttonId <  SignalEmitterMenu.LETTER_BASE + 26) {
            char c = (char) ('A' + (buttonId - SignalEmitterMenu.LETTER_BASE));
            signalType = SignalEmitterMenu.TYPE_CHAR;
            charCode   = c;
            be.setTargetId(String.valueOf(c));
            return true;
        }

        // operand controls
        int newOperand = operand;
        switch (buttonId) {
            case SignalEmitterMenu.BTN_DEC   -> newOperand -= 1;
            case SignalEmitterMenu.BTN_INC   -> newOperand += 1;
            case SignalEmitterMenu.BTN_DEC10 -> newOperand -= 10;
            case SignalEmitterMenu.BTN_INC10 -> newOperand += 10;
            default -> {
                // block selection
                int max = SignalEmitterMenu.getAllBlockIds().size();
                if (buttonId >= SignalEmitterMenu.BLOCK_BASE &&
                        buttonId <  SignalEmitterMenu.BLOCK_BASE + max) {
                    int idx = buttonId - SignalEmitterMenu.BLOCK_BASE;
                    blockIndex = idx;
                    signalType = SignalEmitterMenu.TYPE_BLOCK;
                    String id = "block:" + SignalEmitterMenu.getAllBlockIds().get(blockIndex);
                    be.setTargetId(id);
                    return true;
                }

                // operation buttons
                int maxOps = SignalMathBlockEntity.MathOperation.values().length;
                if (buttonId >= BTN_OP_BASE && buttonId < BTN_OP_BASE + maxOps) {
                    opIndex = buttonId - BTN_OP_BASE;
                    be.setOperationIndex(opIndex);
                    return true;
                }

                // for-each toggle (∃ / ∀)
                if (buttonId == BTN_TOGGLE_FOREACH) {
                    forEachFlag = (forEachFlag == 0 ? 1 : 0);
                    be.setForEach(forEachFlag != 0);
                    return true;
                }

                return false;
            }
        }

        if (newOperand < 0) newOperand = 0;
        if (newOperand > 9999) newOperand = 9999;
        operand = newOperand;
        be.setOperand(newOperand);
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level() == level &&
                player.distanceToSqr(pos.getCenter()) <= 64.0D;
    }
}
