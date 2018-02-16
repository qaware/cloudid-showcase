package de.qaware.cloud.id.util;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.Supplier;

import static java.lang.Math.min;
import static java.lang.Math.pow;

/**
 * Exponential backoff supply strategy.
 *
 * @param <T> value type
 */
@Slf4j
@RequiredArgsConstructor
public class ExponentialBackoffSupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;

    private final Duration initalBackoff;
    private final Duration maxBackoff;
    private final double exponent;

    @SneakyThrows(InterruptedException.class)
    @Override
    public T get() {
        double backoffNs = initalBackoff.toNanos();

        for (; ; ) {
            try {
                return supplier.get();
            } catch (RuntimeException e) {
                LOGGER.error("Error running supplier", e);
            }

            Thread.sleep(Duration.ofNanos((long) backoffNs).toMillis());

            backoffNs = min(maxBackoff.toNanos(), pow(backoffNs, exponent));
        }
    }

}
