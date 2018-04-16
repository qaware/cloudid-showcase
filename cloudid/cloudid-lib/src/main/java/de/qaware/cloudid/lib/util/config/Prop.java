package de.qaware.cloudid.lib.util.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import static de.qaware.cloudid.lib.util.Reflection.getContextClassLoader;
import static java.text.MessageFormat.format;

/**
 * Property.
 *
 * @param <T> value type
 */
@RequiredArgsConstructor
public class Prop<T> {

    private static final Properties DEFAULTS = loadProperties("default-config.properties");

    @Getter
    private final String sysProp;
    private final Function<String, T> converter;

    /**
     * Get the system property value.
     *
     * @return system property value or default value, if the system property is unset.
     */
    public T get() {
        return getOptional()
                .orElseGet(() -> {
                    if (!DEFAULTS.containsKey(sysProp)) {
                        throw new NoSuchElementException(sysProp + " is unset");
                    }
                    return converter.apply(DEFAULTS.getProperty(sysProp));
                });
    }

    /**
     * Get an optional for the system property value.
     *
     * @return optional that is not empty if the system property value is set.
     */
    public Optional<T> getOptional() {
        String value = System.getProperty(sysProp);

        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(converter.apply(value));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    format("Unable to convert system property {0}={1}", sysProp, value),
                    e);
        }
    }

    /**
     * Tells whether this property has been overridden.
     *
     * @return whether this property has been overridden
     */
    public boolean isOverridden() {
        return getOptional().isPresent();
    }


    private static Properties loadProperties(String location) {
        Properties properties;
        try (InputStream inputStream = getContextClassLoader().getResourceAsStream(location)) {

            properties = new Properties();
            properties.load(inputStream);

            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
