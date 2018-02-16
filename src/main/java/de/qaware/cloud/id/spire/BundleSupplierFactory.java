package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.util.ExponentialBackoffSupplier;
import io.grpc.Channel;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

import static de.qaware.cloud.id.spire.Config.*;

/**
 * Factory for bundle suppliers.
 */
@Slf4j
@UtilityClass
class BundleSupplierFactory {

    private static BundleSupplier bundleSupplier;

    /**
     * Get a bundle supplier.
     *
     * @return bundle supplier
     */
    public static synchronized BundleSupplier getBundleSupplier() {
        if (bundleSupplier == null) {
            bundleSupplier = new BundleSupplier(BUNDLES_SUPPLIER_CLASS.get()
                    .orElseGet(() -> new BundlesSupplier(createChannelSupplier())));
            start();
        }

        return bundleSupplier;
    }

    /**
     * Start the bundle supplier.
     */
    public static synchronized void start() {
        bundleSupplier.start();
    }

    /**
     * Stop the bundle supplier.
     */
    public static synchronized void stop() {
        bundleSupplier.stop();
    }

    private static Supplier<Channel> createChannelSupplier() {
        return new ExponentialBackoffSupplier<>(
                new UDSChannelSupplier(AGENT_SOCKET.get()),
                INITIAL_BACKOFF.get(),
                MAX_BACKOFF.get(),
                BACKOFF_EXPONENT.get());
    }

}
