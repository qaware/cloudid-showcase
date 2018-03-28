package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.util.concurrent.RandomExponentialBackoffSupplier;

import java.util.function.Supplier;

import static de.qaware.cloud.id.spire.Config.*;
import static de.qaware.cloud.id.util.Functions.compose;

/**
 * Factory for bundle suppliers.
 */
// Needs to be public as it is to be instantiated via reflection
public class DefaultBundleSupplierFactory implements BundleSupplierFactory {

    private static Supplier<Bundle> bundleSupplier;

    @Override
    public Supplier<Bundle> get() {
        return doGet();
    }

    private static synchronized Supplier<Bundle> doGet() {
        if (bundleSupplier == null) {
            bundleSupplier = createBundleSupplier();
        }

        return bundleSupplier;
    }

    private static Supplier<Bundle> createBundleSupplier() {
        BundlesUpdater bundlesUpdater = new BundlesUpdater(
                new RandomExponentialBackoffSupplier<>(
                        new UdsBundlesSupplier(AGENT_SOCKET.get()),
                        EXP_BACKOFF_BASE.get(),
                        EXP_BACKOFF_STEP.get(),
                        EXP_BACKOFF_RETRIES_CAP.get()));
        bundlesUpdater.start();

        return new BundleSupplier(compose(new BundlesConverter(), bundlesUpdater));
    }

}
