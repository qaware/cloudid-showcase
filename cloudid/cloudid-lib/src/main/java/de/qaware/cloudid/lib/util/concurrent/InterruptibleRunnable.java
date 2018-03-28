package de.qaware.cloudid.lib.util.concurrent;

/**
 * Runnable that may throw InterruptedExceptions.
 */
@FunctionalInterface
public interface InterruptibleRunnable {

    /**
     * Run the runnable.
     *
     * @throws InterruptedException if the current thread has been interrupted
     */
    void run() throws InterruptedException;

}
