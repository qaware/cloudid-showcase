package de.qaware.cloud.id.util.config;

import de.qaware.cloud.id.util.Reflection;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;

/**
 * Utilities for configuration properties.
 */
@UtilityClass
public class Props {

    public static <T> Prop<Class<T>> classOf(String name, Class<T> defaultValue) {
        return new Prop<>(name, Reflection::loadClass, defaultValue);
    }

    /**
     * Create a string property.
     *
     * @param name         system property name
     * @param defaultValue default value
     * @return property
     */
    public static Prop<String> stringOf(String name, String defaultValue) {
        return new Prop<>(name, s -> s, defaultValue);
    }

    /**
     * Create a duration property.
     *
     * @param name         system property name
     * @param defaultValue default value
     * @return property
     */
    public static Prop<Duration> durationOf(String name, Duration defaultValue) {
        return new Prop<>(name, Duration::parse, defaultValue);
    }

    /**
     * Create a double property.
     *
     * @param name         system property name
     * @param defaultValue default value
     * @return property
     */
    public static Prop<Double> doubleOf(String name, Double defaultValue) {
        return new Prop<>(name, Double::valueOf, defaultValue);
    }

    /**
     * Create an integer property.
     *
     * @param name         system property name
     * @param defaultValue default value
     * @return property
     */
    public static Prop<Integer> intOf(String name, Integer defaultValue) {
        return new Prop<>(name, Integer::valueOf, defaultValue);
    }

    /**
     * log all public static {@link Prop} fields of the target class on DEBUG using the classes Logger.
     *
     * @param clazz target class
     */
    public static void debugLog(Class clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        if (logger.isDebugEnabled()) {
            stream(clazz.getDeclaredFields())
                    .filter(f -> Prop.class.isAssignableFrom(f.getType()))
                    .filter(f -> isStatic(f.getModifiers()) && isPublic(f.getModifiers()))
                    .map(f -> Reflection.getValue(f, clazz))
                    .forEach(p -> logger.debug("{}={} ({})",
                            p.getSysProp(),
                            p.get(),
                            p.isOverridden() ? "override" : "default"));
        }
    }

}
