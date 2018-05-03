package org.aerogear.mobile.core.reactive;

import java.util.concurrent.Callable;

/**
 * Factory class for creating {@link Request} objects.
 */
public final class Requester {
    private Requester() {}


    /**
     * Create a Request and supply a method to cleanup any resources.
     *
     * @param value a constant value to be produced for each responder
     * @param <T> the value of the return type of callable
     * @return a chainable request object
     */
    public static <T> Request<T> emit(T value) {
        return new CallableRequest<>(() -> value);
    }


    /**
     * Create a Request
     *
     * @param callable a callable that will generate a value
     * @param <T> the value of the return type of callable
     * @return a chainable request object
     */
    public static <T> Request<T> call(Callable<T> callable) {
        return new CallableRequest<>(callable);
    }

    /**
     * Create a Request and supply a method to cleanup any resources.
     *
     * @param callable a callable that will generate a value
     * @param cleanupFunction a method that will be called after callable, after responses are
     *        called, and on the same thread
     * @param <T> the value of the return type of callable
     * @return a chainable request object
     */
    public static <T> Request<T> call(Callable<T> callable, Cleaner cleanupFunction) {
        return new CallableRequest<T>(callable, cleanupFunction);
    }

}
