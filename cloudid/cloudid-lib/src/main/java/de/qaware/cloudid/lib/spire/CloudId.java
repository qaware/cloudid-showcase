package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.util.Reflection;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Provided facilities for dealing with workload identities.
 */
@Slf4j
@UtilityClass
public class CloudId {

    private static CloudIdManager cloudIdManager;

    /**
     * Get the bundles manager.
     *
     * @return bundles manager
     */
    public static synchronized CloudIdManager getManager() {
        if (cloudIdManager == null) {
            cloudIdManager = Reflection.instantiate(Config.CLOUD_ID_MANAGER_CLASS.get());
            cloudIdManager.start();
        }

        return cloudIdManager;
    }

    /**
     * Reset the static launcher.
     * <p>
     * Should only be used for tests.
     */
    public static synchronized void reset() {
        LOGGER.warn("Resetting {}", CloudId.class.getSimpleName());
        cloudIdManager.stop();
        cloudIdManager = null;
    }

}
