package de.qaware.cloudid.lib.util.concurrent;

import de.qaware.cloudid.lib.util.Reference;

import java.util.concurrent.CountDownLatch;

import static de.qaware.cloudid.lib.util.concurrent.Concurrent.run;

/**
 * Reference that blocks until a value becomes available.
 *
 * @param <T> value type
 */
public class BlockingReference<T> implements Reference<T> {

    private final CountDownLatch setLatch = new CountDownLatch(1);
    private volatile T value;

    @Override
    public T get() {
        run(setLatch::await);
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
        setLatch.countDown();
    }

}
