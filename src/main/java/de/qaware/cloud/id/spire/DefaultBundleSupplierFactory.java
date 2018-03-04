package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.util.RandomExponentialBackoffSupplier;

import java.util.function.Supplier;

import static de.qaware.cloud.id.spire.Config.*;
import static de.qaware.cloud.id.util.Functions.compose;

/**
 * Factory for bundle suppliers.
 */
class DefaultBundleSupplierFactory implements BundleSupplierFactory {

    private static Supplier<Bundle> bundleSupplier;
    private static BundlesUpdater bundlesUpdater;

    @Override
    public Supplier<Bundle> get() {
        return doGet();
    }

    @Override
    public void start() {
        doStart();
    }

    @Override
    public void stop() {
        doStop();
    }

    private static synchronized void doStop() {
        init();
        bundlesUpdater.stop();
    }

    private static synchronized void doStart() {
        init();
        bundlesUpdater.start();
    }

    private static synchronized Supplier<Bundle> doGet() {
        init();
        return bundleSupplier;
    }

    private static synchronized void init() {
        if (bundleSupplier == null) {
            bundlesUpdater = new BundlesUpdater(
                    new RandomExponentialBackoffSupplier<>(
                            new UdsBundlesSupplier(AGENT_SOCKET.get()),
                            EXP_BACKOFF_BASE.get(),
                            EXP_BACKOFF_STEP.get(),
                            EXP_BACKOFF_RETRIES_CAP.get()));

            bundleSupplier = new BundleSupplier(compose(new BundlesConverter(), bundlesUpdater));
        }
    }

}
