package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.util.config.Prop;
import de.qaware.cloud.id.util.config.Props;
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
    public static final Prop<Duration> FORCE_UPDATE_AFTER = Props.durationOf("spire.forceUpdateAfter", Duration.ofMinutes(15));
    /**
     * Duration an update should be done before the bundles expire.
     */
    public static final Prop<Duration> UPDATE_AHEAD = Props.durationOf("spire.updateAhead", Duration.ofMinutes(1));
    /**
     * Minimum update interval as a safety measure if the TTLs are too short.
     */
    public static final Prop<Duration> MIN_UPDATE_INTERVAL = Props.durationOf("spire.updateAhead", Duration.ofSeconds(30));

    /**
     * Get the bundle supplier factory class.
     */
    public static final Prop<Class<DefaultBundleSupplierFactory>> BUNDLE_SUPPLIER_FACTORY_CLASS = Props.classOf("spire.bundleSupplierClass", DefaultBundleSupplierFactory.class);

}
