package de.qaware.cloud.id.util.concurrent;

import de.qaware.cloud.id.util.Reference;

import java.util.concurrent.CountDownLatch;

import static de.qaware.cloud.id.util.concurrent.Concurrent.run;

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
        setLatch.countDown();
        this.value = value;
    }

}
