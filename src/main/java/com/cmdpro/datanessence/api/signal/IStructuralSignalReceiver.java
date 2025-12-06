package com.cmdpro.datanessence.api.signal;

public interface IStructuralSignalReceiver {
    /**
     * Called by a structural node when a signal reaches this block.
     */
    void acceptSignal(String signalId, int amount);
}
