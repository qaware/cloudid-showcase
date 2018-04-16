package de.qaware.cloudid.lib;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * List of identities for a workload.
 */
@Data
public class Bundles {

    private final List<Bundle> bundleList;
    private final Instant expiry;

    /**
     * Tells whether this bundle is empty.
     * @return whether this bundle is empty
     */
    public boolean isEmpty() {
        return bundleList.isEmpty();
    }

}
