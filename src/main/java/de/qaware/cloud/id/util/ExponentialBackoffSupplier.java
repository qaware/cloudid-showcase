package de.qaware.cloud.id.util;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Exponential backoff supply strategy.
 *
 * @param <T> value type
 */
@Slf4j
public class ExponentialBackoffSupplier<T> implements InterruptibleSupplier<T> {

    private final Supplier<T> supplier;
    private final long initalMs;
    private final long maxMs;
    private final double exp;

    /**
     * Constructor.
     *
     * @param supplier delegate supplier
     * @param initalMs initial backoff in ms
     * @param maxMs    max. backoff in ms
     * @param exp      backoff exponent
     */
    public ExponentialBackoffSupplier(Supplier<T> supplier, long initalMs, long maxMs, double exp) {
        this.supplier = supplier;
        this.initalMs = initalMs;
        this.maxMs = maxMs;
        this.exp = exp;
    }

    @Override
    public T get() throws InterruptedException {
        long backoff = initalMs;
        for (; ; ) {
            try {
                return supplier.get();
            } catch (RuntimeException e) {
                LOGGER.error("Error running supplier", e);
            }

            Thread.sleep(backoff);

            backoff = Math.min(maxMs, (long) Math.pow(backoff, exp));
        }
    }

}
