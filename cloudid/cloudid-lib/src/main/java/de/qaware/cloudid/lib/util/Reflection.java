package de.qaware.cloudid.lib.util;

import de.qaware.cloudid.lib.util.config.Prop;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

/**
 * Reflection utilities.
 */
@UtilityClass
public class Reflection {

    /**
     * Load a class without type checks using the current thread's context class loader.
     *
     * @param name class name
     * @param <T>  class type
     * @return class
     */
    @SuppressWarnings({"unchecked", "squid:S2658"})
    public static <T> Class<T> loadClass(String name) {
        try {
            return (Class<T>) getContextClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Instantiate a class.
     *
     * @param clazz class
     * @param <T>   instance type
     * @return instance
     */
    public static <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Get a field's value.
     *
     * @param field  field
     * @param holder field holder (instance or class)
     * @return value
     */
    public static Prop getValue(Field field, Object holder) {
        try {
            return (Prop) field.get(holder);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get the current thread's context class loader.
     *
     * @return current thread's context class loader
     */
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
