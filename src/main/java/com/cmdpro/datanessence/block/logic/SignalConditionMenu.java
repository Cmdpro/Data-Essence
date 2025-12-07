package com.cmdpro.datanessence.block.logic;

import com.cmdpro.datanessence.registry.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SignalConditionMenu extends AbstractContainerMenu {

    public static final int BTN_DIGIT_BASE   = 0;   // 0..9
    public static final int BTN_LETTER_BASE  = 10;  // 10..35

    public static final int BTN_DEC      = 100;
    public static final int BTN_INC      = 101;
    public static final int BTN_DEC10    = 102;
    public static final int BTN_INC10    = 103;

    public static final int BTN_COMP_LT  = 110;
    public static final int BTN_COMP_EQ  = 111;
    public static final int BTN_COMP_GT  = 112;

    public static final int BTN_MODE_ID  = 120;
    public static final int BTN_MODE_ANY = 121; // ∃
    public static final int BTN_MODE_ALL = 122; // ∀

    private final BlockPos pos;
    private final Level level;

    private int idMode;
    private int idChar;
    private int comparison;
    private int threshold;

    public SignalConditionMenu(int id, Inventory inv, AbstractSignalConditionBlockEntity be) {
        super(MenuRegistry.SIGNAL_CONDITION_MENU.get(), id);
        this.pos = be.getBlockPos();
        this.level = be.getLevel();

        this.idMode = be.getIdMode();
        this.idChar = be.getIdChar();
        this.comparison = be.getComparison();
        this.threshold = be.getThreshold();

        addDataSlots();
    }

    public SignalConditionMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(MenuRegistry.SIGNAL_CONDITION_MENU.get(), id);
        this.pos = buf.readBlockPos();
        this.level = inv.player.level();

        this.idMode = AbstractSignalConditionBlockEntity.ID_MODE_SPECIFIC;
        this.idChar = 'X';
        this.comparison = AbstractSignalConditionBlockEntity.COMP_EQUAL;
        this.threshold = 1;

        addDataSlots();
    }

    private void addDataSlots() {
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return idMode; }
            @Override public void set(int value) { idMode = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return idChar; }
            @Override public void set(int value) { idChar = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return comparison; }
            @Override public void set(int value) { comparison = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return threshold; }
            @Override public void set(int value) { threshold = value; }
        });
    }

    private AbstractSignalConditionBlockEntity getBE() {
        if (level == null) return null;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AbstractSignalConditionBlockEntity cond) return cond;
        return null;
    }

    public int getIdMode() {
        return idMode;
    }

    public char getIdChar() {
        return (char) idChar;
    }

    public int getComparison() {
        return comparison;
    }

    public int getThreshold() {
        return threshold;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        AbstractSignalConditionBlockEntity be = getBE();
        if (be == null) return false;

        if (buttonId >= BTN_DIGIT_BASE && buttonId < BTN_DIGIT_BASE + 10) {
            char c = (char) ('0' + (buttonId - BTN_DIGIT_BASE));
            idChar = c;
            idMode = AbstractSignalConditionBlockEntity.ID_MODE_SPECIFIC;
            be.setIdChar(idChar);
            be.setIdMode(idMode);
            return true;
        }

        if (buttonId >= BTN_LETTER_BASE && buttonId < BTN_LETTER_BASE + 26) {
            char c = (char) ('A' + (buttonId - BTN_LETTER_BASE));
            idChar = c;
            idMode = AbstractSignalConditionBlockEntity.ID_MODE_SPECIFIC;
            be.setIdChar(idChar);
            be.setIdMode(idMode);
            return true;
        }

        switch (buttonId) {
            case BTN_MODE_ID -> {
                idMode = AbstractSignalConditionBlockEntity.ID_MODE_SPECIFIC;
                be.setIdMode(idMode);
                return true;
            }
            case BTN_MODE_ANY -> {
                idMode = AbstractSignalConditionBlockEntity.ID_MODE_ANY;
                be.setIdMode(idMode);
                return true;
            }
            case BTN_MODE_ALL -> {
                idMode = AbstractSignalConditionBlockEntity.ID_MODE_ALL;
                be.setIdMode(idMode);
                return true;
            }

            case BTN_COMP_LT -> {
                comparison = AbstractSignalConditionBlockEntity.COMP_LESS;
                be.setComparison(comparison);
                return true;
            }
            case BTN_COMP_EQ -> {
                comparison = AbstractSignalConditionBlockEntity.COMP_EQUAL;
                be.setComparison(comparison);
                return true;
            }
            case BTN_COMP_GT -> {
                comparison = AbstractSignalConditionBlockEntity.COMP_GREATER;
                be.setComparison(comparison);
                return true;
            }

            case BTN_DEC, BTN_INC, BTN_DEC10, BTN_INC10 -> {
                int delta =
                        buttonId == BTN_DEC ? -1 :
                                buttonId == BTN_INC ? 1 :
                                        buttonId == BTN_DEC10 ? -10 : 10;
                int newThr = threshold + delta;
                if (newThr < 0) newThr = 0;
                if (newThr > 9999) newThr = 9999;
                threshold = newThr;
                be.setThreshold(newThr);
                return true;
            }

            default -> {
                return false;
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level() == level &&
                player.distanceToSqr(pos.getCenter()) <= 64.0D;
    }
}
