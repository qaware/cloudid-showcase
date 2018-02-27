package de.qaware.cloud.id.util;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Random;
import java.util.function.Supplier;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.String.format;

/**
 * Exponential backoff supply strategy.
 *
 * @param <T> value type
 */
@Slf4j
@RequiredArgsConstructor
public class ExponentialBackoffSupplier<T> implements Supplier<T> {

    private final Random random = new Random();

    private final Supplier<T> supplier;
    private final double base;
    private final Duration step;
    private final int retriesCap;


    @SneakyThrows(InterruptedException.class)
    @Override
    public T get() {
        int retries = 1;

        for (; ; ) {
            try {
                return supplier.get();
            } catch (RuntimeException e) {
                long backoffMs = round(random.nextDouble() * step.toMillis() * pow(base, retries));

                LOGGER.error(format("Error running supplier, backing off for %ds after %d retries",
                        Duration.ofMillis(backoffMs).getSeconds(), retries), e);
                Thread.sleep(backoffMs);
            }

            if (retries < retriesCap) {
                ++retries;
            }
        }
    }

}
