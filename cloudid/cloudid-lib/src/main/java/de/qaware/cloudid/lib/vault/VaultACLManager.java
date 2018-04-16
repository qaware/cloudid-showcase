package de.qaware.cloudid.lib.vault;

import de.qaware.cloudid.lib.ACL;
import de.qaware.cloudid.lib.ACLManager;
import de.qaware.cloudid.lib.Config;
import de.qaware.cloudid.util.AsyncUpdater;
import de.qaware.cloudid.util.concurrent.RandomExponentialBackoffSupplier;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static de.qaware.cloudid.lib.Config.*;

/**
 * Retrieves the ACL from Vault using VAULT_ADDRESS property {@link Config} to connect to a running Vault server
 */
@Slf4j
public class VaultACLManager extends AsyncUpdater<ACL> implements ACLManager {

    private static final Duration BACKOFF = Duration.ofMinutes(5);
    private static final String THREAD_NAME = "cloudid-acl-updater";

    /**
     * Constructor
     */
    public VaultACLManager() {
        super(new RandomExponentialBackoffSupplier<>(
                        new VaultACLSupplier(),
                        VAULT_EXP_BACKOFF_BASE.get(),
                        VAULT_EXP_BACKOFF_STEP.get(),
                        VAULT_EXP_BACKOFF_RETRIES_CAP.get()),
                a -> BACKOFF,
                THREAD_NAME);
    }

}
