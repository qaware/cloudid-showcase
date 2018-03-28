package de.qaware.cloudid.lib.util;

import lombok.experimental.UtilityClass;

/**
 * Utilities for dealing with comparables.
 */
@UtilityClass
public class Comparables {

    /**
     * Get the minimum of two durations.
     *
     * @param a duration a
     * @param b duration b
     * @return a if a is less than b, b otherwise
     */
    public static <T extends Comparable<? super T>> T min(T a, T b) {
        if (a.compareTo(b) < 0) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * Get the maximum of two durations.
     *
     * @param a duration a
     * @param b duration b
     * @return a if a is greater or equal than b, b otherwise
     */
    public static <T extends Comparable<? super T>> T max(T a, T b) {
        if (a.compareTo(b) >= 0) {
            return a;
        } else {
            return b;
        }
    }

}
