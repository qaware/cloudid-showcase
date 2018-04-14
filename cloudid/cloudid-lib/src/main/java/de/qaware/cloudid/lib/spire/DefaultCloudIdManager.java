package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.util.concurrent.RandomExponentialBackoffSupplier;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static de.qaware.cloudid.lib.spire.Config.*;
import static de.qaware.cloudid.lib.util.Comparables.max;
import static de.qaware.cloudid.lib.util.Comparables.min;
import static de.qaware.cloudid.lib.util.Functions.compose;
import static de.qaware.cloudid.lib.util.concurrent.Concurrent.*;
import static java.time.Instant.now;

/**
 * Default bundles manager.
 */
@Slf4j
public class DefaultCloudIdManager implements CloudIdManager {

    private static final String THREAD_NAME = "cloudid-updater";

    private final Collection<Consumer<Bundles>> listeners = new ArrayList<>();

    private Thread updater;
    private volatile Bundles bundles;
    private final CountDownLatch setLatch = new CountDownLatch(1);

    private final Supplier<Bundles> supplier = compose(new BundlesConverter(),
            new RandomExponentialBackoffSupplier<>(
                    new UdsBundlesSupplier(AGENT_SOCKET.get()),
                    EXP_BACKOFF_BASE.get(),
                    EXP_BACKOFF_STEP.get(),
                    EXP_BACKOFF_RETRIES_CAP.get()));

    @Override
    public synchronized void start() {
        if (updater != null) {
            throw new IllegalStateException("Already running.");
        }

        updater = new Thread(() -> repeat(this::update), THREAD_NAME);
        updater.setDaemon(true);
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
        listeners.add(listener);
        if (bundles != null) {
            listener.accept(bundles);
        }
    }

    private void update() {
        Bundles newBundles = supplier.get();

        if (!Objects.equals(newBundles, this.bundles)) {
            this.bundles = newBundles;
            setLatch.countDown();
            notifyListeners(newBundles);
        }

        Duration backoff = min(
                max(Duration.between(now(), bundles.getExpiry()), MIN_UPDATE_INTERVAL.get()),
                FORCE_UPDATE_AFTER.get());

        LOGGER.debug("Backing off for {}", backoff);
        sleep(backoff);
    }

    private synchronized void notifyListeners(Bundles bundles) {
        listeners.forEach(l -> l.accept(bundles));
    }

}
