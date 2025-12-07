package com.cmdpro.datanessence.api.signal;

import java.util.Map;

/**
 * Something that can receive structural signals.
 */
public interface IStructuralSignalReceiver {

    /**
     * New multi-signal receive call.
     */
    default void acceptSignals(Map<String, Integer> signals) {
        signals.forEach(this::acceptSignal);
    }

    /**
     * Old single-signal style callback (optional).
     */
    default void acceptSignal(String id, int amount) {
        // default: do nothing
    }
}
