package com.perceivedev.perceivecore.util;

/**
 * A Function taking three inputs and having one output
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {

    /**
     * Applies the function
     * 
     * @param t The first parameter
     * @param u The second parameter
     * @param v The third parameter
     * @return The result of applying the function
     */
    R apply(T t, U u, V v);
}
