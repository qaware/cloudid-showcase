package de.qaware.cloudid.lib;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static de.qaware.cloudid.lib.util.Reflection.instantiate;

/**
 * Provided facilities for dealing with workload identities.
 */
@Slf4j
@UtilityClass
public class CloudId {

    /**
     * CloudId algorithm.
     */
    public static final String ALGORITHM = "CloudId";
    /**
     * CloudId trust store algorithm.
     */
    public static final String TRUST_STORE_ALGORITHM = ALGORITHM + "-TrustStore";

    /**
     * Fixed alias for the single identity currently supported.
     */
    public static final String SINGLE_ALIAS = "spiffe";

    /**
     * Provider name.
     */
    public static final String PROVIDER_NAME = "cloudid-provider";
    /**
     * Provider version.
     */
    public static final double PROVIDER_VERSION = 0.1;
    /**
     * Provider description.
     */
    public static final String PROVIDER_DESCRIPTION = "";

    private static IdManager idManager;
    private static ACLManager aclManager;

    /**
     * Get the Id manager.
     *
     * @return Id manager
     */
    public static synchronized IdManager getIdManager() {
        if (idManager == null) {
            idManager = instantiate(Config.ID_MANAGER_CLASS.get());
            idManager.start();
        }

        return idManager;
    }

    /**
     * Get the ACL manager.
     *
     * @return ACL manager
     */
    public static synchronized ACLManager getAclManager() {
        if (aclManager == null) {
            aclManager = instantiate(Config.ACL_MANAGER_CLASS.get());
            aclManager.start();
        }

        return aclManager;
    }

    /**
     * Reset the static launcher.
     * <p>
     * Should only be used for tests.
     */
    public static synchronized void reset() {
        LOGGER.warn("Resetting {}", CloudId.class.getSimpleName());

        if (idManager != null) {
            idManager.stop();
            idManager = null;
        }

        if (aclManager != null) {
            aclManager.stop();
            aclManager = null;
        }
    }

}
