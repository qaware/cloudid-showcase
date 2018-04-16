package de.qaware.cloudid.lib.util;

import de.qaware.cloudid.lib.Updater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static de.qaware.cloudid.lib.util.concurrent.Concurrent.*;

/**
 * Asynchronous updater
 *
 * @param <T> value type
 */
@Slf4j
@RequiredArgsConstructor
public class AsyncUpdater<T> implements Updater<T> {

    private final Collection<Consumer<T>> listeners = new ArrayList<>();
    private final CountDownLatch setLatch = new CountDownLatch(1);

    private final Supplier<T> valueSupplier;
    private final Function<T, Duration> backoffSupplier;
    private final String threadName;

    private Thread updaterThread;
    private volatile T value;


    @Override
    public T get() {
        run(setLatch::await);
        return value;
    }

    @Override
    public final synchronized void start() {
        if (updaterThread != null) {
            throw new IllegalStateException("Already running.");
        }

        updaterThread = new Thread(() -> repeat(this::update), threadName);
        updaterThread.setDaemon(true);
        updaterThread.start();
    }

    @Override
    public final synchronized void stop() {
        if (updaterThread == null) {
            throw new IllegalStateException("Not running.");
        }

        updaterThread.interrupt();
    }

    @Override
    public final synchronized void addListener(Consumer<T> listener) {
        listeners.add(listener);
        if (value != null) {
            listener.accept(value);
        }
    }

    private void update() {
        T newValue = valueSupplier.get();

        if (!Objects.equals(newValue, value)) {
            this.value = newValue;
            setLatch.countDown();
            notifyListeners(newValue);
        }

        Duration backoff = backoffSupplier.apply(newValue);

        LOGGER.debug("{} backing off for {}", threadName, backoff);
        sleep(backoff);
    }

    private synchronized void notifyListeners(T bundles) {
        listeners.forEach(l -> l.accept(bundles));
    }

}
