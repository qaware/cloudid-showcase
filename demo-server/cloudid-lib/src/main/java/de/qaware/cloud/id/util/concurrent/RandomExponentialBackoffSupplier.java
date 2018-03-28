package de.qaware.cloud.id.util.concurrent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Random;
import java.util.function.Supplier;

import static de.qaware.cloud.id.util.concurrent.Concurrent.sleep;
import static java.lang.Math.*;
import static java.lang.String.format;

/**
 * Exponential random backoff supply strategy.
 * <p>
 * Exponential random backoff is calculated as such, with retries starting at 0:
 * <p>
 * <pre>
 * step * random[1, 2] * base^min(retries, retriesCap)
 * </pre>
 * <p>
 * If interrupted, the supplier sneakily throws an {@link InterruptedException}
 *
 * @param <T> value type
 */
@Slf4j
@RequiredArgsConstructor
public class RandomExponentialBackoffSupplier<T> implements Supplier<T> {

    // We don't need cryptographically sound random numbers
    @SuppressWarnings("squid:S2245")
    private final Random random = new Random();

    private final Supplier<T> supplier;
    private final double base;
    private final Duration step;
    private final int retriesCap;

    @Override
    public T get() {
        for (long retries = 0; ; ++retries) {
            try {
                return supplier.get();
            } catch (RuntimeException e) {
                long backoffMs = round(step.toMillis()
                        * (1. + random.nextDouble())
                        * pow(base, min(retries, retriesCap)));

                LOGGER.error(format("Error running supplier, backing off for %ds after %d retries",
                        Duration.ofMillis(backoffMs).getSeconds(), retries), e);

                sleep(backoffMs);
            }
        }
    }

}
