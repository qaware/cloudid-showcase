package de.qaware.cloud.id.util;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Exponential backoff supply strategy.
 *
 * @param <T> value type
 */
@Slf4j
@RequiredArgsConstructor
public class ExponentialBackoffSupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private final long initalMs;
    private final long maxMs;
    private final double exp;

    @SneakyThrows
    @Override
    public T get() {
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
