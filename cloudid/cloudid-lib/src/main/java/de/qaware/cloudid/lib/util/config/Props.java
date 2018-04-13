package de.qaware.cloudid.lib.util.config;

import de.qaware.cloudid.lib.util.Reflection;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.regex.Pattern;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Utilities for configuration properties.
 */
@UtilityClass
public class Props {

    private static final Pattern PWD_PATTERN = Pattern.compile("pwd|password", CASE_INSENSITIVE);
    private static final String PWD_REPLACEMENT = "...";

    /**
     * Create a class property
     *
     * @param name         system property name
     * @param defaultValue default value
     * @param <T>          class type
     * @return property
     */
    @SuppressWarnings("unchecked")
    public static <T> Prop<Class<T>> classOf(String name, Class<? extends T> defaultValue) {
        return new Prop<>(name, Reflection::loadClass, (Class) defaultValue);
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
     * Create an boolean property.
     *
     * @param name         system property name
     * @param defaultValue default value
     * @return property
     */
    public static Prop<Boolean> booleanOf(String name, Boolean defaultValue) {
        return new Prop<>(name, Boolean::parseBoolean, defaultValue);
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
                            maskPasswords(p),
                            p.isOverridden() ? "override" : "default"));
        }
    }

    private static Object maskPasswords(Prop p) {
        return PWD_PATTERN.matcher(p.getSysProp()).find()? PWD_REPLACEMENT : p.get();
    }

}
