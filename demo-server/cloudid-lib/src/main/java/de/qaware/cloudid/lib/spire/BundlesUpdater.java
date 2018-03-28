package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.util.Reference;
import de.qaware.cloudid.lib.util.concurrent.BlockingReference;
import lombok.extern.slf4j.Slf4j;
import spire.api.workload.WorkloadOuterClass;

import java.time.Duration;
import java.util.function.Supplier;

import static de.qaware.cloudid.lib.spire.Config.*;
import static de.qaware.cloudid.lib.util.Comparables.max;
import static de.qaware.cloudid.lib.util.Comparables.min;
import static de.qaware.cloudid.lib.util.concurrent.Concurrent.repeat;
import static de.qaware.cloudid.lib.util.concurrent.Concurrent.sleep;

/**
 * Updates bundles.
 */
@Slf4j
class BundlesUpdater implements Supplier<WorkloadOuterClass.Bundles> {

    private static final String THREAD_NAME = "spire-updater";

    private final Reference<WorkloadOuterClass.Bundles> bundlesRef = new BlockingReference<>();
    private final Supplier<WorkloadOuterClass.Bundles> bundlesSupplier;
    private Thread updater;

    /**
     * Constructor.
     *
     * @param bundlesSupplier bundles supplier.
     */
    BundlesUpdater(Supplier<WorkloadOuterClass.Bundles> bundlesSupplier) {
        this.bundlesSupplier = bundlesSupplier;
    }

    @Override
    public WorkloadOuterClass.Bundles get() {
        return bundlesRef.get();
    }

    /**
     * Start the bundles updater.
     */
    public synchronized void start() {
        if (updater != null) {
            throw new IllegalStateException("Already running.");
        }

        updater = new Thread(() -> repeat(this::update), THREAD_NAME);
        updater.start();
    }

    /**
     * Stop the bundles updater.
     */
    public synchronized void stop() {
        if (updater == null) {
            throw new IllegalStateException("Not running.");
        }

        updater.interrupt();
    }

    private void update() {
        WorkloadOuterClass.Bundles bundles = bundlesSupplier.get();

        bundlesRef.set(bundles);

        Duration backoff = min(
                max(Duration.ofSeconds(bundles.getTtl()).minus(UPDATE_AHEAD.get()), MIN_UPDATE_INTERVAL.get()),
                FORCE_UPDATE_AFTER.get());

        LOGGER.debug("Backing off for {}", backoff);
        sleep(backoff);
    }

}
