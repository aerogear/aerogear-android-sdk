package org.aerogear.mobile.core.reactive;

/**
 * A function that takes a value and returns nothing.
 *
 * @param <T> the type of the value the function uses.
 */
@FunctionalInterface
public interface VoidReturnWithParam<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void execute(T t);
}
