package de.qaware.cloud.id.spire;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.lang.Math.min;
import static java.time.Instant.now;
import static java.util.Optional.empty;

/**
 * Provides up-to-date bundles.
 * <p>
 * Updates the backing bundles according to the expiry of the last bundle.
 * Selects the bundle with the max. time to life while skipping bundles that have not yet become valid.
 */
@Slf4j
@RequiredArgsConstructor
public class BundleSupplier implements Supplier<SVIDBundle> {

    private static final String THREAD_NAME = "spiffe-updater";

    private final Supplier<Bundles> bundlesSupplier;

    private final AtomicReference<Optional<Bundles>> bundlesRef = new AtomicReference<>(empty());
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
        return getBundleList().stream()
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
            interrupt(e);
        }
        updaterThread = null;
    }

    // Intentionally catching Throwable as the stack trace would be lost otherwise

    @SuppressWarnings("squid:S1181")
    private void updater() {
        try {
            while (running.get()) {
                Bundles bundles = bundlesSupplier.get();

                bundlesRef.set(Optional.of(bundles));

                Instant bundleExpiry = bundles.getExpiry();
                Instant now = now();

                // Log if the bundle is expired
                if (bundleExpiry.isBefore(now)) {
                    LOGGER.error("Received a bundle that has expired on {}",
                            LocalDateTime.ofInstant(bundleExpiry, ZoneId.systemDefault()));
                }

                // Backoff until the newest bundle expires.
                Thread.sleep(min(
                        Duration.between(now, bundleExpiry.minus(Config.UPDATE_AHEAD.get())).toMillis(),
                        Config.FORCE_UPDATE_AFTER.get().toMillis()));
            }
        } catch (InterruptedException e) {
            interrupt(e);
        } catch (Throwable e) {
            LOGGER.error("Updater died unexpectedly", e);
        }
    }

    private List<SVIDBundle> getBundleList() {
        return bundlesRef.get()
                .orElseThrow(IllegalStateException::new)
                .getBundleList();
    }

    private static void interrupt(InterruptedException e) {
        LOGGER.info("Interrupted", e);
        Thread.currentThread().interrupt();
    }

}
