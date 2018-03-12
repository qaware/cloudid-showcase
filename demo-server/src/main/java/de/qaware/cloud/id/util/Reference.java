package de.qaware.cloud.id.util;

/**
 * A mutable reference.
 *
 * @param <T> value type
 */
public interface Reference<T> {

    /**
     * Get the value referred to.
     *
     * @return value
     */
    T get();

    /**
     * Set the value referred to
     *
     * @param value value
     */
    void set(T value);

}
