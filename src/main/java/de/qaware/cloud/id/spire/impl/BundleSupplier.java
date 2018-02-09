package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.SVIDBundle;
import de.qaware.cloud.id.util.ExponentialBackoffSupplyStrategy;
import de.qaware.cloud.id.util.SupplyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static com.google.common.base.Verify.verify;
import static java.lang.Math.min;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

/**
 * Provides up-to-date bundles.
 */
public class BundleSupplier implements Supplier<SVIDBundle> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleSupplier.class);

    private final SupplyStrategy<List<SVIDBundle>> bundleSupplier = new ExponentialBackoffSupplyStrategy<>(
            this::update,
            2_000,
            60_000,
            1.5);

    private Thread updaterThread;
    private final AtomicBoolean running = new AtomicBoolean();

    private final BundleFetcher bundleFetcher;
    private final Duration forceUpdateAfter;
    private final Duration updateAhead;

    private List<SVIDBundle> bundles = emptyList();

    /**
     * Constructor.
     *
     * @param bundleFetcher    bundle updater
     * @param forceUpdateAfter force an update after this time
     * @param updateAhead      update bundles this duration before expiry
     */
    public BundleSupplier(BundleFetcher bundleFetcher, Duration forceUpdateAfter, Duration updateAhead) {
        this.bundleFetcher = bundleFetcher;
        this.forceUpdateAfter = forceUpdateAfter;
        this.updateAhead = updateAhead;
    }

    /**
     * Get the bundle.
     *
     * @return bundle
     */
    @Override
    public SVIDBundle get() {
        Instant now = now();

        // This selects the first tuple with that is valid, preferring tuples with longer validity.
        // Bundles are sorted descending by notAfter.
        return getBundles(now).stream()
                .filter(b -> b.getNotBefore().isBefore(now()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * Start the updater.
     */
    public synchronized void start() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("Already running");
        }

        updaterThread = new Thread(this::updater);
        updaterThread.start();
    }

    /**
     * Stop the updater
     */
    public synchronized void stop() {
        if (!running.compareAndSet(true, false)) {
            throw new IllegalStateException("Not running");
        }

        updaterThread.interrupt();
        try {
            updaterThread.join();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        updaterThread = null;
    }

    private synchronized void setBundles(List<SVIDBundle> bundles) {
        this.bundles = bundles;
    }

    private synchronized List<SVIDBundle> getBundles(Instant now) {
        return bundles;
    }

    private List<SVIDBundle> update() {
        List<SVIDBundle> bundles = bundleFetcher.fetchBundle();

        // Verify the assumption that this workload has exactly one SPIFFE Id
        verify(bundles.stream().map(SVIDBundle::getSvId).collect(toSet()).size() != 1,
                "This workload must receive exactly one SPIFFE Id");

        // Sort descending by notAfter
        bundles.sort((a, b) -> b.getNotAfter().compareTo(a.getNotAfter()));

        return bundles;
    }

    private void updater() {
        try {
            while (running.get()) {
                List<SVIDBundle> bundles = bundleSupplier.get();

                setBundles(bundles);

                Instant bundleExpiry = bundles.get(bundles.size() - 1).getNotAfter().minus(updateAhead);
                Thread.sleep(min(between(now(), bundleExpiry).toMillis(), forceUpdateAfter.toMillis()));
            }
        } catch (InterruptedException e) {
            LOGGER.trace("Interrupted", e);
        } catch (Throwable e) {
            LOGGER.error("Updater died unexpectedly", e);
        }
    }

}
