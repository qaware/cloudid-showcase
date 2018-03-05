package de.qaware.cloud.id.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;

/**
 * Utilities for dealing with dates/times.
 */
@UtilityClass
public class Time {

    /**
     * Get the minimum of two durations.
     *
     * @param a duration a
     * @param b duration b
     * @return a if a is less than b, b otherwise
     */
    public static Duration min(Duration a, Duration b) {
        if (a.compareTo(b) < 0) {
            return a;
        } else {
            return b;
        }
    }

}
