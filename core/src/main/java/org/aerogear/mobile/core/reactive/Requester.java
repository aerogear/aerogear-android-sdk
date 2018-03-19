package org.aerogear.mobile.core.reactive;

import java.util.concurrent.Callable;

/**
 * Factory class for creating {@link Request} objects.
 */
public final class Requester {
    private Requester() {}

    public static <T> Request<T> emit(T value) {
        return new CallableRequest<>(() -> value);
    }

    public static <T> Request<T> call(Callable<T> callable) {
        return new CallableRequest<>(callable);
    }
}
