package de.qaware.cloud.id.util.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;

/**
 * Property.
 *
 * @param <T> value type
 */
@RequiredArgsConstructor
public class Prop<T> {

    @Getter
    private final String sysProp;
    private final Function<String, T> converter;
    @Getter
    private final T defaultValue;

    /**
     * Get the system property value.
     *
     * @return system property value or default value, if the system property is unset.
     */
    public T get() {
        return getOptional().orElse(defaultValue);
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
            throw new IllegalArgumentException(format("Unable to convert system property %s=%s", sysProp, value), e);
        }
    }

    /**
     * Tells whether this property has been overridden.
     * @return whether this property has been overridden
     */
    public boolean isOverridden() {
        return getOptional().isPresent();
    }

}
