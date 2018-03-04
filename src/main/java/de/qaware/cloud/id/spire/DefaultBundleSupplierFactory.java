package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.util.RandomExponentialBackoffSupplier;
import spire.api.workload.WorkloadOuterClass;

import java.util.function.Supplier;

import static de.qaware.cloud.id.spire.Config.*;
import static de.qaware.cloud.id.util.Functions.compose;

/**
 * Factory for bundle suppliers.
 */
class DefaultBundleSupplierFactory implements BundleSupplierFactory {

    private static Supplier<SVIDBundle> bundleSupplier;
    private static BundlesUpdater bundlesUpdater;

    @Override
    public Supplier<SVIDBundle> get() {
        return doGet();
    }

    @Override
    public void start() {
        bundlesUpdater.start();
    }

    @Override
    public void stop() {
        bundlesUpdater.stop();
    }

    private static synchronized Supplier<SVIDBundle> doGet() {
        if (bundleSupplier == null) {
            bundleSupplier = new BundleSupplier(compose(new BundlesConverter(), withUpdate(withBackoff(fromUds()))));
        }

        return bundleSupplier;
    }

    private static UdsBundlesSupplier fromUds() {
        return new UdsBundlesSupplier(AGENT_SOCKET.get());
    }

    private static Supplier<WorkloadOuterClass.Bundles> withUpdate(Supplier<WorkloadOuterClass.Bundles> supplier) {
        bundlesUpdater = new BundlesUpdater(supplier);
        return bundlesUpdater;
    }

    private static <T> Supplier<T> withBackoff(Supplier<T> supplier) {
        return new RandomExponentialBackoffSupplier<>(supplier,
                EXP_BACKOFF_BASE.get(),
                EXP_BACKOFF_STEP.get(),
                EXP_BACKOFF_RETRIES_CAP.get());
    }

}
