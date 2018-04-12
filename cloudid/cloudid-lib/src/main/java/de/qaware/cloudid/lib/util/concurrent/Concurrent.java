package de.qaware.cloudid.lib.util.concurrent;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * Utilities for dealing with concurrency.
 */
@Slf4j
@UtilityClass
public class Concurrent {

    /**
     * Sleep this thread, re-interrupting on a  InterruptedException.
     *
     * @param millis sleep duration in milliseconds
     */
    public static void sleep(long millis) {
        run(() -> Thread.sleep(millis));
    }

    /**
     * Sleep this thread, re-interrupting on a  InterruptedException.
     *
     * @param duration sleep duration
     */
    public static void sleep(Duration duration) {
        sleep(duration.toMillis());
    }

    /**
     * Re-interrupt the current thread if a runnable throws a InterruptedException.
     *
     * @param runnable interruptible runnable
     */
    public static void run(InterruptibleRunnable runnable) {
        try {
            runnable.run();
        } catch (InterruptedException e) {
            LOGGER.info("Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Repeatedly call a runnable until the thread has been interrupted.
     *
     * @param runnable runnable
     */
    @SuppressWarnings("squid:S2221" /* Stack traces would be lost otherwise */)
    public static void repeat(InterruptibleRunnable runnable) {
        try {
            while (!Thread.interrupted()) {
                run(runnable);
            }
        } catch (Exception e) {
            LOGGER.error("{} died unexpectedly", Thread.currentThread().getName(), e);
        }
    }


}

