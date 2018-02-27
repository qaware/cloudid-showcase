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
    static final Supplier<String> AGENT_SOCKET = stringOf("spire.agentSocket", "/spire/socket/agent.sock");

    /**
     * Step duration for the exponential backoff.
     *
     * Exponential backoff is calculated as such: {@code random(step * base^max(retries, retriesCap))}.
     */
    static final Supplier<Duration> EXP_BACKOFF_STEP = durationOf("spire.agent.socket.expBackoff.step", Duration.ofSeconds(1));
    /**
     * Base for the exponential backoff.
     *
     * Exponential backoff is calculated as such: {@code random(step * base^max(retries, retriesCap))}.
     */
    static final Supplier<Double> EXP_BACKOFF_BASE = doubleOf("spire.agent.socket.expBackoff.base", 2.);
    /**
     * Number of retries after which exponential backoff growth is capped.
     *
     * Exponential backoff is calculated as such: {@code random(step * base^max(retries, retriesCap))}.
     */
    static final Supplier<Integer> EXP_BACKOFF_RETRIES_CAP = intOf("spire.agent.socket.expBackoff.retriesCap", 6);

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
    static final Supplier<Optional<Supplier<Bundles>>> BUNDLES_SUPPLIER_CLASS = instanceOf("spire.bundlesSupplierClass");


    private static <T> Supplier<Optional<T>> instanceOf(String name) {
        return () -> get(name, Config::instantiate);
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

    private static Supplier<Integer> intOf(String name, Integer defaultValue) {
        return () -> get(name, Integer::valueOf).orElse(defaultValue);
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
