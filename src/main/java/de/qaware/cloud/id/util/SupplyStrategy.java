package de.qaware.cloud.id.util;

/**
 * Supply strategy.
 *
 * @param <T> value type
 */
public interface SupplyStrategy<T> {

    /**
     * Get a value.
     *
     * @return value
     * @throws InterruptedException if the current thread has been interrupted
     */
    T get() throws InterruptedException;

}
