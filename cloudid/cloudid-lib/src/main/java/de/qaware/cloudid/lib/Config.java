package de.qaware.cloudid.lib;

import de.qaware.cloudid.util.config.Prop;
import lombok.experimental.UtilityClass;

import java.time.Duration;

import static de.qaware.cloudid.util.config.Props.*;

/**
 * Utilities for dealing with system properties.
 */
@UtilityClass
public class Config {

    /**
     * UNIX domain socket used to connect to the SPIRE agent.
     */
    public static final Prop<String> SPIRE_AGENT_SOCKET = stringOf("cloudid.spire.agentSocket");
    /**
     * Base for the exponential backoff when querying SPIRE.
     */
    public static final Prop<Double> SPIRE_EXP_BACKOFF_BASE = doubleOf("cloudid.spire.expBackoff.base");
    /**
     * Step duration for the exponential backoff when querying SPIRE.
     */
    public static final Prop<Duration> SPIRE_EXP_BACKOFF_STEP = durationOf("cloudid.spire.expBackoff.step");
    /**
     * Number of retries after which exponential backoff growth is capped when querying SPIRE.
     */
    public static final Prop<Integer> SPIRE_EXP_BACKOFF_RETRIES_CAP = intOf("cloudid.spire.expBackoff.retriesCap");


    /**
     * Whether to disable ACL validation.
     */
    public static final Prop<Boolean> ACL_DISABLED = booleanOf("cloudid.disableAcl");
    /**
     * The address for accessing Vault.
     */
    public static final Prop<String> VAULT_ADDRESS = stringOf("cloudid.vault.address");
    /**
     * The path where the ACL secret is deposited in vault (default: secret/acl)
     */
    public static final Prop<String> VAULT_ACL_SECRET_PATH = stringOf("cloudid.vault.aclPath");
    /**
     * Base for the exponential backoff when querying Vault.
     */
    public static final Prop<Double> VAULT_EXP_BACKOFF_BASE = doubleOf("cloudid.vault.expBackoff.base");
    /**
     * Step duration for the exponential backoff when querying Vault.
     */
    public static final Prop<Duration> VAULT_EXP_BACKOFF_STEP = durationOf("cloudid.vault.expBackoff.step");
    /**
     * Number of retries after which exponential backoff growth is capped when querying Vault.
     */
    public static final Prop<Integer> VAULT_EXP_BACKOFF_RETRIES_CAP = intOf("cloudid.vault.expBackoff.retriesCap");


    /**
     * Get the ACL supplier factory class.
     */
    public static final Prop<Class<ACLManager>> ACL_MANAGER_CLASS = classOf("cloudid.aclManagerClass");
    /**
     * Get the bundles manager class.
     */
    public static final Prop<Class<IdManager>> ID_MANAGER_CLASS = classOf("cloudid.idManagerClass");


    /**
     * Debug keystore location.
     */
    public static final Prop<String> DEBUG_KEYSTORE_LOCATION = stringOf("cloudid.debug.keystore.location");
    /**
     * Debug keystore type.
     */
    public static final Prop<String> DEBUG_KEYSTORE_TYPE = stringOf("cloudid.debug.keystore.type");
    /**
     * Debug keystore password.
     */
    public static final Prop<String> DEBUG_KEYSTORE_PASSWORD = stringOf("cloudid.debug.keystore.password");
    /**
     * Debug keystore alias.
     */
    public static final Prop<String> DEBUG_KEYSTORE_ALIAS = stringOf("cloudid.debug.keystore.alias");
    /**
     * Debug key password.
     */
    public static final Prop<String> DEBUG_KEY_PASSWORD = stringOf("cloudid.debug.key.password");
}
