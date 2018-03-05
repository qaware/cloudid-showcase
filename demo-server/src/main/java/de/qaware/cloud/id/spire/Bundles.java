package de.qaware.cloud.id.spire;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * SPIRE Bundles.
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
