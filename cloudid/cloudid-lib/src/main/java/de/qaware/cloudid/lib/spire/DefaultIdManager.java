package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.Bundles;
import de.qaware.cloudid.lib.IdManager;
import de.qaware.cloudid.util.AsyncUpdater;
import de.qaware.cloudid.util.concurrent.RandomExponentialBackoffSupplier;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static de.qaware.cloudid.lib.Config.*;
import static de.qaware.cloudid.util.Comparables.max;
import static de.qaware.cloudid.util.Functions.compose;
import static java.time.Instant.now;

/**
 * Default Id manager.
 */
@Slf4j
public class DefaultIdManager extends AsyncUpdater<Bundles> implements IdManager {

    private static final String THREAD_NAME = "cloudid-bundle-updater";
    private static final Duration MIN_BACKOFF = Duration.ofSeconds(30);

    /**
     * Constructor.
     */
    public DefaultIdManager() {
        super(compose(new BundlesConverter(),
                new RandomExponentialBackoffSupplier<>(
                        new UdsBundlesSupplier(SPIRE_AGENT_SOCKET.get()),
                        SPIRE_EXP_BACKOFF_BASE.get(),
                        SPIRE_EXP_BACKOFF_STEP.get(),
                        SPIRE_EXP_BACKOFF_RETRIES_CAP.get())),
                b -> max(Duration.between(now(), b.getExpiry()), MIN_BACKOFF),
                THREAD_NAME);
    }

}
