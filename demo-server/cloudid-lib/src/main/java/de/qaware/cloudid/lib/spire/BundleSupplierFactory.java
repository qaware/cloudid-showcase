package de.qaware.cloudid.lib.spire;

import java.util.function.Supplier;

/**
 * Bundle supplier factory.
 */
public interface BundleSupplierFactory {

    /**
     * Get a bundle supplier.
     *
     * @return bundle supplier
     */
    Supplier<Bundle> get();

}
