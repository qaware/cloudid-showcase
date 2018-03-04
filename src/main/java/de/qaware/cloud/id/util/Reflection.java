package de.qaware.cloud.id.util;

import de.qaware.cloud.id.util.config.Prop;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

/**
 * Reflection utilities.
 */
@UtilityClass
public class Reflection {

    /**
     * Load a class without type checks using the default class loader.
     *
     * @param name class name
     * @param <T>  class type
     * @return class
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> loadClass(String name) {
        try {
            return (Class<T>) Class.forName(name);
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
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Prop getValue(Field f, Object o) {
        try {
            return  (Prop) f.get(o);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
