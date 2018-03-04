package de.qaware.cloud.id.spire;

import java.util.function.Supplier;

/**
 * Bundle supplier factory.
 */
public interface BundleSupplierFactory {

    /**
     * Get a bundle supplier.
     * @return bundle supplier
     */
    Supplier<SVIDBundle> get();

    /**
     * Lifecycle of the bundle supplier begins.
     */
    default void start() {
    }

    /**
     * Lifecycle of the bundle supplier ends.
     */
    default void stop() {
    }

}
