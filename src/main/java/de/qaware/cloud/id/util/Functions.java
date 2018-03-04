package de.qaware.cloud.id.util;

import lombok.experimental.UtilityClass;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides utilities for dealing with functional interfaces.
 */
@UtilityClass
public class Functions {

    /**
     * Compose a function and a supplier.
     *
     * @param f   function
     * @param s   original supplier
     * @param <T> original suppliers result type
     * @param <R> resulting suppliers result type
     * @return {@code () -> f.apply(s.get()}
     */
    public static <T, R> Supplier<R> compose(Function<? super T, R> f, Supplier<T> s) {
        return () -> f.apply(s.get());
    }

}
