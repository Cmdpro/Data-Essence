package com.cmdpro.datanessence.api.signal;

import java.util.Collections;
import java.util.Map;

/**
 * Provides structural “signals” to structural nodes.
 *
 * New multi-signal API: getSignals() returns (id -> amount) for EVERYTHING.
 * Old single-signal methods still exist for compatibility.
 */
public interface IStructuralSignalProvider {

    /**
     * Old single-signal id. Can be ignored if you override getSignals().
     */
    default String getSignalId() {
        return null;
    }

    /**
     * Old single-signal amount. Can be ignored if you override getSignals().
     */
    default int getSignalAmount() {
        return 0;
    }

    /**
     * New multi-signal API.
     * Default implementation wraps the old pair into a single-entry map.
     */
    default Map<String, Integer> getSignals() {
        String id = getSignalId();
        int amount = getSignalAmount();
        if (id == null || amount <= 0) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap(id, amount);
    }
}
