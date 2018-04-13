package de.qaware.cloudid.lib.spire;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spire.api.workload.WorkloadOuterClass;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static de.qaware.cloudid.lib.spire.Config.FORCE_UPDATE_AFTER;
import static de.qaware.cloudid.lib.spire.Config.MIN_UPDATE_INTERVAL;
import static de.qaware.cloudid.lib.util.Comparables.max;
import static de.qaware.cloudid.lib.util.Comparables.min;
import static de.qaware.cloudid.lib.util.concurrent.Concurrent.*;
import static java.time.Instant.now;

/**
 * Default bundles manager.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultCloudIdManager implements CloudIdManager {

    private static final String THREAD_NAME = "cloudid-updater";

    private static final BundlesConverter BUNDLES_CONVERTER = new BundlesConverter();

    private final Collection<Consumer<Bundles>> listeners = new ArrayList<>();

    private Thread updater;
    private volatile Bundles bundles;
    private final CountDownLatch setLatch = new CountDownLatch(1);

    private final Supplier<WorkloadOuterClass.Bundles> bundlesSupplier;

    @Override
    public synchronized void start() {
        if (updater != null) {
            throw new IllegalStateException("Already running.");
        }

        updater = new Thread(() -> repeat(this::update), THREAD_NAME);
        updater.start();
    }

    @Override
    public synchronized void stop() {
        if (updater == null) {
            throw new IllegalStateException("Not running.");
        }

        updater.interrupt();
    }

    @Override
    public Bundles getBundles() {
        run(setLatch::await);

        return bundles;
    }

    @Override
    public synchronized void addListener(Consumer<Bundles> listener) {
        this.listeners.add(listener);
    }

    private void update() {
        Bundles newBundles = BUNDLES_CONVERTER.apply(bundlesSupplier.get());

        if (!Objects.equals(newBundles, this.bundles)) {
            this.bundles = newBundles;
            setLatch.countDown();
            listeners.forEach(l -> l.accept(newBundles));
        }

        Duration backoff = min(
                max(Duration.between(now(), bundles.getExpiry()), MIN_UPDATE_INTERVAL.get()),
                FORCE_UPDATE_AFTER.get());

        LOGGER.debug("Backing off for {}", backoff);
        sleep(backoff);
    }

}
