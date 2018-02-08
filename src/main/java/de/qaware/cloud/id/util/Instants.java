package de.qaware.cloud.id.util;

import java.time.Instant;

/**
 * Utilities for instants.
 */
public class Instants {

    /**
     * Selects the instant occurring first.
     *
     * @param a instant a
     * @param b instant b
     * @return a.isBefore(b)? a: b
     */
    public static Instant min(Instant a, Instant b) {
        return a.isBefore(b) ? a : b;
    }

    /**
     * No instantiation.
     */
    private Instants() {

    }

}
