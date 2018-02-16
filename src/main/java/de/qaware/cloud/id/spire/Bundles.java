package de.qaware.cloud.id.spire;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * SPIRE Bundles.
 */
@Data
public class Bundles {

    private final List<SVIDBundle> bundles;
    private final Instant expiry;

}
