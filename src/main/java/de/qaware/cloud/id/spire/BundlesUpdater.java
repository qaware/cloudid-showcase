package de.qaware.cloud.id.spire;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spire.api.workload.WorkloadOuterClass;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static de.qaware.cloud.id.spire.Config.FORCE_UPDATE_AFTER;
import static de.qaware.cloud.id.spire.Config.UPDATE_AHEAD;
import static de.qaware.cloud.id.util.Time.min;
import static java.util.Optional.empty;

/**
 * Updates bundles.
 */
@SuppressWarnings("squid:S2142" /* Rule is broken as it cannot handle interrupt handling in methods */)
@Slf4j
@RequiredArgsConstructor
class BundlesUpdater implements Supplier<WorkloadOuterClass.Bundles> {

    private static final String THREAD_NAME = "spire-updater";

    private final AtomicReference<Optional<WorkloadOuterClass.Bundles>> bundlesRef = new AtomicReference<>(empty());
    private final AtomicBoolean running = new AtomicBoolean();

    private final Supplier<WorkloadOuterClass.Bundles> bundlesSupplier;

    private Thread updaterThread;

    @Override
    public WorkloadOuterClass.Bundles get() {
        return bundlesRef.get()
                .orElseThrow(() -> new IllegalStateException("No bundles available (yet)"));
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

    private void updater() {
        try {
            while (running.get()) {
                WorkloadOuterClass.Bundles bundles = bundlesSupplier.get();

                bundlesRef.set(Optional.of(bundles));

                Duration backoff = min(
                        Duration.ofSeconds(bundles.getTtl()).minus(UPDATE_AHEAD.get()),
                        FORCE_UPDATE_AFTER.get());

                LOGGER.debug("Backing off for {}", backoff);
                Thread.sleep(backoff.toMillis());
            }
        } catch (InterruptedException e) {
            interrupt(e);
        } catch (Exception e) {
            LOGGER.error("Updater died unexpectedly", e);
        }
    }

    private static void interrupt(InterruptedException e) {
        LOGGER.info("Interrupted", e);
        Thread.currentThread().interrupt();
    }
}
