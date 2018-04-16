package de.qaware.cloudid.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Interface for updaters.
 * <p>
 * Updaters provide up-to-date values and allow lsiteners to listen for value changes.
 *
 * @param <T> value type
 */
public interface Updater<T> extends Supplier<T> {

    @Override
    T get();

    /**
     * Add a listener that gets notified whenever the updated value changes.
     * <p>
     * Listeners will be notified immediately if a value was available before they were added.
     *
     * @param listener listener
     */
    void addListener(Consumer<T> listener);

    /**
     * Start updating.
     */
    void start();

    /**
     * Stop updating.
     */
    void stop();

}
