package com.cmdpro.datanessence.api.signal;

public interface IStructuralSignalProvider {
    /**
     * Identifier of the signal (for now, just "x").
     * Later this can be something like "number:5", "block:minecraft:stone", etc.
     */
    String getSignalId();

    /**
     * Quantity / strength of the signal.
     */
    int getSignalAmount();
}
