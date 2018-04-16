package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.util.config.Prop;
import de.qaware.cloudid.lib.util.config.Props;
import de.qaware.cloudid.lib.vault.ACLFactory;
import de.qaware.cloudid.lib.vault.VaultACLFactory;
import lombok.experimental.UtilityClass;

import java.time.Duration;

/**
 * Utilities for dealing with system properties.
 */
@UtilityClass
public class Config {

    /**
     * UNIX domain socket used to connect to the SPIRE agent.
     */
    public static final Prop<String> AGENT_SOCKET = Props.stringOf("spire.agentSocket", "/spire/socket/agent.sock");

    /**
     * Step duration for the exponential backoff.
     */
    public static final Prop<Duration> EXP_BACKOFF_STEP = Props.durationOf("spire.expBackoff.step", Duration.ofSeconds(1));
    /**
     * Base for the exponential backoff.
     */
    public static final Prop<Double> EXP_BACKOFF_BASE = Props.doubleOf("spire.expBackoff.base", 2.);
    /**
     * Number of retries after which exponential backoff growth is capped.
     */
    public static final Prop<Integer> EXP_BACKOFF_RETRIES_CAP = Props.intOf("spire.expBackoff.retriesCap", 5);

    /**
     * Duration after which to force an update.
     */
    public static final Prop<Duration> FORCE_UPDATE_AFTER = Props.durationOf("spire.forceUpdateAfter", Duration.ofDays(1));
    /**
     * Minimum update interval as a safety measure if the TTLs are too short.
     */
    public static final Prop<Duration> MIN_UPDATE_INTERVAL = Props.durationOf("spire.updateAhead", Duration.ofSeconds(30));

    /**
     * Get the bundles manager class.
     */
    public static final Prop<Class<CloudIdManager>> CLOUD_ID_MANAGER_CLASS = Props.classOf("cloudid.bundlesManagerClass", DefaultCloudIdManager.class);

    /**
     * Get the ACL supplier factory class.
     */
    public static final Prop<Class<ACLFactory>> ACL_SUPPLIER_CLASS = Props.classOf("spire.aclSupplierClass", VaultACLFactory.class);

    /**
     * Whether to disable ACL validation.
     */
    public static final Prop<Boolean> ACL_DISABLED = Props.booleanOf("spire.disableAcl", false);

    /**
     * The address for accessing Vault.
     */
    public static final Prop<String> VAULT_ADDRESS = Props.stringOf("cloudid.vault.address", "https://localhost:8200");

    /**
     * The path where the ACL secret is deposited in vault (default: secret/acl)
     */
    public static final Prop<String> VAULT_ACL_SECRET_PATH = Props.stringOf("cloudid.vault.aclPath", "secret/acl");

}
