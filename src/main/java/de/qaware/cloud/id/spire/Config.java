package de.qaware.cloud.id.spire;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utilities for dealing with system properties.
 */
@Slf4j
@UtilityClass
class Config {

    /**
     * UNIX domain socket used to connect to the SPIRE agent.
     */
    static final Supplier<String> AGENT_SOCKET = stringOf("spire.agentSocket", "/Volumes/Cloud-ID/codebase/spire-k8s/socket/agent.sock");

    /**
     * initial backoff for polling the agent socket.
     */
    static final Supplier<Duration> INITIAL_BACKOFF = durationOf("spire.agent.socket.initialBackoff", Duration.ofSeconds(2));
    /**
     * Max. backoff for polling the agent socket.
     */
    static final Supplier<Duration> MAX_BACKOFF = durationOf("spire.agent.socket.maxBackoff", Duration.ofSeconds(60));
    /**
     * Backoff exponent for polling the agent socket.
     */
    static final Supplier<Double> BACKOFF_EXPONENT = doubleOf("spire.agent.socket.backoffExponent", 1.2);

    /**
     * Duration after which to force an update.
     */
    static final Supplier<Duration> FORCE_UPDATE_AFTER = durationOf("spire.bundles.forceUpdateAfter", Duration.ofMinutes(15));
    /**
     * Duration an update should be done before the bundles expire.
     */
    static final Supplier<Duration> UPDATE_AHEAD = durationOf("spire.bundles.updateAhead", Duration.ofMinutes(1));

    /**
     * Get an overridden bundles supplier if set.
     */
    static final Supplier<Optional<Supplier<Bundles>>> BUNDLES_SUPPLIER_CLASS = () -> instanceOf("spire.bundlesSupplierClass");


    private static <T> Optional<T> instanceOf(String name) {
        return get(name, Config::instantiate);
    }

    private static Supplier<String> stringOf(String name, String defaultValue) {
        return () -> get(name, s -> s).orElse(defaultValue);
    }

    private static Supplier<Duration> durationOf(String name, Duration defaultValue) {
        return () -> get(name, Duration::parse).orElse(defaultValue);
    }

    private static Supplier<Double> doubleOf(String name, Double defaultValue) {
        return () -> get(name, Double::valueOf).orElse(defaultValue);
    }

    @SuppressWarnings({"unchecked", "squid:S2658"})
    private static <T> T instantiate(String name) {
        try {
            return (T) Class.forName(name).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static <T> Optional<T> get(String name, Function<String, T> converter) {
        String value = System.getProperty(name);

        if (value == null) {
            return Optional.empty();
        }

        LOGGER.debug("{}={}", name, value);

        try {
            return Optional.of(converter.apply(value));
        } catch (RuntimeException e) {
            LOGGER.error("Unable to convert system property {}={}", name, value);
            throw new IllegalArgumentException(e);
        }
    }

}
