package de.qaware.cloud.id.util;

/**
 * Interruptible supplier.
 *
 * @param <T> value type
 */
public interface InterruptibleSupplier<T> {

    /**
     * Get a value.
     *
     * @return value
     * @throws InterruptedException if the current thread has been interrupted
     */
    T get() throws InterruptedException;

}
