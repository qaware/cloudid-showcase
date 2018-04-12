package de.qaware.cloudid.lib.util.concurrent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static de.qaware.cloudid.lib.util.concurrent.Concurrent.sleep;
import static java.lang.Math.*;

/**
 * Exponential random backoff supply strategy.
 * <p>
 * Exponential random backoff is calculated as such, with retries starting at 0:
 *
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
                        * ThreadLocalRandom.current().nextDouble(1., 2.)
                        * pow(base, min(retries, retriesCap)));

                LOGGER.error("Error running supplier, backing off for {}s after {} retries",
                        Duration.ofMillis(backoffMs).getSeconds(), retries, e);

                sleep(backoffMs);
            }
        }
    }

}
