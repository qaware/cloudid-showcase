package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.SVIDBundle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static com.google.common.base.Verify.verify;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.time.Instant.now;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

/**
 * Provides up-to-date bundles.
 */
@Slf4j
@RequiredArgsConstructor
public class BundleSupplier implements Supplier<SVIDBundle> {

    /**
     * Minimum backoff for polling bundles.
     */
    private static final Duration MIN_BACKOFF = Duration.ofSeconds(30);

    private static final String THREAD_NAME = "spiffe-bundle-updater";

    private final Supplier<List<SVIDBundle>> bundlesSupplier;
    private final Duration forceUpdateAfter;
    private final Duration updateAhead;

    private final AtomicReference<List<SVIDBundle>> bundles = new AtomicReference<>(emptyList());
    private final AtomicBoolean running = new AtomicBoolean();

    private Thread updaterThread;


    /**
     * Get the bundle.
     *
     * @return bundle
     */
    @Override
    public SVIDBundle get() {
        // This selects the first tuple with that is valid, preferring tuples with longer validity.
        // Bundles are sorted descending by notAfter.
        Instant now = now();
        return this.bundles.get().stream()
                .filter(b -> b.getNotBefore().isBefore(now))
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

        updaterThread = new Thread(this::updater, THREAD_NAME);
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

    private void updater() {
        try {
            while (running.get()) {
                List<SVIDBundle> bundles = fetchBundles();

                this.bundles.set(bundles);

                Instant bundleExpiry = bundles.get(0).getNotAfter();
                Instant now = now();

                // Log if the bundle is expired
                if (bundleExpiry.isBefore(now)) {
                    LOGGER.error("Received a bundle that expired on {}",
                            LocalDateTime.ofInstant(bundleExpiry, ZoneId.systemDefault())
                    );
                }

                // Backoff until the newest bundle expires minus a safety period
                // Use a minimum backoff to prevent swamping the supplier if the supplier
                // does not provide bundles that live long enough.
                Thread.sleep(max(
                        min(
                                Duration.between(now, bundleExpiry.minus(updateAhead)).toMillis(),
                                forceUpdateAfter.toMillis()),
                        MIN_BACKOFF.toMillis()));
            }
        } catch (InterruptedException e) {
            LOGGER.trace("Interrupted", e);
        } catch (Throwable e) {
            LOGGER.error("Updater died unexpectedly", e);
        }
    }

    private List<SVIDBundle> fetchBundles() {
        List<SVIDBundle> bundles = bundlesSupplier.get();

        // Verify the assumption that this workload has exactly one SPIFFE Id
        verify(bundles.stream().map(SVIDBundle::getSvId).collect(toSet()).size() == 1,
                "This workload must receive exactly one SPIFFE Id");

        // Sort descending by notAfter
        bundles.sort((a, b) -> b.getNotAfter().compareTo(a.getNotAfter()));

        return bundles;
    }

}
