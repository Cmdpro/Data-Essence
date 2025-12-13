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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class SignalEmitterMenu extends AbstractContainerMenu implements ISignalConfigMenu {

    //button id ranges

    public static final int DIGIT_BASE   = 0;   // 0..9   -> '0'..'9'
    public static final int LETTER_BASE  = 10;  // 10..35 -> 'A'..'Z'
    public static final int BTN_DEC      = 100; // amount -1
    public static final int BTN_INC      = 101; // amount +1
    public static final int BTN_DEC10    = 102; // amount -10
    public static final int BTN_INC10    = 103; // amount +10

    public static final int BTN_TAB_CHARS  = 190;
    public static final int BTN_TAB_BLOCKS = 191;

    public static final int BLOCK_BASE   = 1000; // BLOCK_BASE + index

    //signal type

    public static final int TYPE_CHAR  = 0;
    public static final int TYPE_BLOCK = 1;

    //tabs (UI only)

    public static final int TAB_CHARS  = 0;
    public static final int TAB_BLOCKS = 1;

    //static block list (same order on server & client)

    private static final List<Block> ALL_BLOCKS = BuiltInRegistries.BLOCK.stream()
            .filter(b -> b.asItem() != Items.AIR)
            .toList();

    private static final List<ResourceLocation> ALL_BLOCK_IDS = ALL_BLOCKS.stream()
            .map(b -> BuiltInRegistries.BLOCK.getKey(b))
            .toList();

    public static List<Block> getAllBlocks() {
        return ALL_BLOCKS;
    }

    public static List<ResourceLocation> getAllBlockIds() {
        return ALL_BLOCK_IDS;
    }

    private static int indexOfBlock(ResourceLocation id) {
        for (int i = 0; i < ALL_BLOCK_IDS.size(); i++) {
            if (ALL_BLOCK_IDS.get(i).equals(id)) return i;
        }
        return 0;
    }

    //instance fields

    private final BlockPos pos;
    private final Level level;

    // synced via DataSlot
    private int tabIndex;    // which TAB is open (chars / blocks), UI only
    private int signalType;  // what the signal actually is (char / block)
    private int charCode;    // current char selection
    private int amount;      // current amount
    private int blockIndex;  // index in ALL_BLOCK_IDS for block signal

    // SERVER constructor
    public SignalEmitterMenu(int id, Inventory inv, SignalEmitterBlockEntity emitter) {
        super(MenuRegistry.SIGNAL_EMITTER.get(), id);
        this.pos = emitter.getBlockPos();
        this.level = emitter.getLevel();

        String sig = emitter.getSignalId();
        if (sig != null && sig.startsWith("block:")) {
            signalType = TYPE_BLOCK;
            tabIndex = TAB_BLOCKS; // open blocks tab by default for block signals

            ResourceLocation blockId = emitter.getBlockSignalId();
            if (blockId != null) {
                blockIndex = indexOfBlock(blockId);
            } else {
                blockIndex = 0;
            }
            charCode = 'X'; // irrelevant in block mode
        } else {
            signalType = TYPE_CHAR;
            tabIndex = TAB_CHARS;

            charCode = (sig != null && !sig.isEmpty()) ? sig.charAt(0) : 'X';
            blockIndex = 0;
        }

        amount = emitter.getSignalAmount();

        addDataSlots();
    }

    // CLIENT constructor (called via IContainerFactory)
    public SignalEmitterMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(MenuRegistry.SIGNAL_EMITTER.get(), id);
        this.pos = buf.readBlockPos();
        this.level = inv.player.level();

        // defaults; overwritten by DataSlots from server
        this.tabIndex   = TAB_CHARS;
        this.signalType = TYPE_CHAR;
        this.charCode   = 'X';
        this.amount     = 1;
        this.blockIndex = 0;

        addDataSlots();
    }

    private void addDataSlots() {
        // tab index (UI, but needs to be synced so client knows which tab is selected)
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return tabIndex; }
            @Override public void set(int value) { tabIndex = value; }
        });

        // signal type
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return signalType; }
            @Override public void set(int value) { signalType = value; }
        });

        // char code
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return charCode; }
            @Override public void set(int value) { charCode = value; }
        });

        // amount
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return amount; }
            @Override public void set(int value) { amount = value; }
        });

        // block index
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockIndex; }
            @Override public void set(int value) { blockIndex = value; }
        });
    }

    private SignalEmitterBlockEntity getEmitter() {
        if (level == null) return null;
        BlockEntity be = level.getBlockEntity(pos);
        return (be instanceof SignalEmitterBlockEntity e) ? e : null;
    }

    //API for screen

    @Override
    public boolean isBlockMode() {
        return tabIndex == TAB_BLOCKS;
    }

    @Override
    public boolean isBlockSignal() {
        return signalType == TYPE_BLOCK;
    }

    @Override
    public String getSignalId() {
        if (signalType == TYPE_BLOCK) {
            if (blockIndex < 0 || blockIndex >= ALL_BLOCK_IDS.size()) return "block:?";
            return "block:" + ALL_BLOCK_IDS.get(blockIndex);
        }
        return String.valueOf((char) charCode);
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public int getBlockIndex() {
        return blockIndex;
    }

    //button handling (SERVER side)

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        SignalEmitterBlockEntity emitter = getEmitter();
        if (emitter == null) return false;

        // Tabs - ONLY change tabIndex, DO NOT touch signalType or signal value
        if (buttonId == BTN_TAB_CHARS) {
            tabIndex = TAB_CHARS;
            return true;
        }
        if (buttonId == BTN_TAB_BLOCKS) {
            tabIndex = TAB_BLOCKS;
            return true;
        }

        // digits 0-9 -> explicit change of signal
        if (buttonId >= DIGIT_BASE && buttonId < DIGIT_BASE + 10) {
            char c = (char) ('0' + (buttonId - DIGIT_BASE));
            signalType = TYPE_CHAR;
            charCode = c;
            emitter.setCharSignal(c);
            return true;
        }

        // letters A-Z -> explicit change of signal
        if (buttonId >= LETTER_BASE && buttonId < LETTER_BASE + 26) {
            char c = (char) ('A' + (buttonId - LETTER_BASE));
            signalType = TYPE_CHAR;
            charCode = c;
            emitter.setCharSignal(c);
            return true;
        }

        // amount controls - do not care about signal type
        int newAmount = amount;
        switch (buttonId) {
            case BTN_DEC   -> newAmount -= 1;
            case BTN_INC   -> newAmount += 1;
            case BTN_DEC10 -> newAmount -= 10;
            case BTN_INC10 -> newAmount += 10;
            default -> {
                // block selection?
                if (buttonId >= BLOCK_BASE && buttonId < BLOCK_BASE + ALL_BLOCK_IDS.size()) {
                    int idx = buttonId - BLOCK_BASE;
                    blockIndex = idx;
                    signalType = TYPE_BLOCK;
                    emitter.setBlockSignal(ALL_BLOCK_IDS.get(blockIndex));
                    return true;
                }
                return false;
            }
        }

        if (newAmount < 0) newAmount = 0;
        if (newAmount > 9999) newAmount = 9999;

        amount = newAmount;
        emitter.setAmount(newAmount);
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level() == level
                && player.distanceToSqr(pos.getCenter()) <= 64.0D;
    }
}
