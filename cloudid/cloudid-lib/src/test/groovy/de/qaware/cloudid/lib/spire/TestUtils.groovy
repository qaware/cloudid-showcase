package de.qaware.cloudid.lib.spire

import de.qaware.cloudid.lib.CloudId

import java.time.Duration
import java.time.Instant

class TestUtils {

    /**
     * Wait until a bundle is available.
     *
     * There is a race condition if tests are allowed to proceed before the test bundle is available.
     *
     * @param timeout timeout
     */
    static void waitUntilBundleIsAvailable(Duration timeout) {
        Instant start = Instant.now()

        while (Duration.between(start, Instant.now()) < timeout) {
            try {
                CloudId.getIdManager().getWorkloadId()
                return
            } catch (IllegalStateException ignored) {
                Thread.sleep(100)
            }
        }

        throw new IllegalStateException("WorkloadId not available after ${timeout}")
    }

}
