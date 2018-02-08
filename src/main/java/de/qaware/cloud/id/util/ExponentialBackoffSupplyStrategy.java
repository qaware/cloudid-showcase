package de.qaware.cloud.id.util;

import de.qaware.cloud.id.spire.impl.BundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Exponential backoff supply strategy.
 *
 * @param <T> value type
 */
public class ExponentialBackoffSupplyStrategy<T> implements SupplyStrategy<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleProvider.class);

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
    public ExponentialBackoffSupplyStrategy(Supplier<T> supplier,
                                            long initalMs, long maxMs, double exp) {
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
