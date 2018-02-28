package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.util.ExponentialBackoffSupplier;
import de.qaware.cloud.id.util.NettySocket;
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
            LOGGER.debug("Socket type: {}", NettySocket.CURRENT);

            if (!NettySocket.CURRENT.domainSocketsSupported()) {
                LOGGER.error("No domain socket support on this system. Connecting to the SPIRE agent will not work.");
            }

            bundleSupplier = new BundleSupplier(BUNDLES_SUPPLIER_CLASS.get()
                    .orElseGet(BundleSupplierFactory::createBundlesSupplier));
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

    private static Supplier<Bundles> createBundlesSupplier() {
        return new ExponentialBackoffSupplier<>(
                new BundlesSupplier(AGENT_SOCKET.get()),
                EXP_BACKOFF_BASE.get(),
                EXP_BACKOFF_STEP.get(),
                EXP_BACKOFF_RETRIES_CAP.get());
    }

}
