package de.qaware.cloud.id.util;

import lombok.NoArgsConstructor;

import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

/**
 * Utilities for instants.
 */
@NoArgsConstructor(access = PRIVATE)
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

}
