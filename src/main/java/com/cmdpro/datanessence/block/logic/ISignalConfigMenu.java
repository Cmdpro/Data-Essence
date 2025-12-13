package com.cmdpro.datanessence.block.logic;

public interface ISignalConfigMenu {
    boolean isBlockMode();
    boolean isBlockSignal();
    String getSignalId();
    int getAmount();      // for emitter: amount; for math: operand
    int getBlockIndex();
}
