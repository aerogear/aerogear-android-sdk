package org.aerogear.mobile.core.reactive;

import java.util.concurrent.Callable;

import org.aerogear.mobile.core.Request;

/**
 * Factory class for creating {@link org.aerogear.mobile.core.Request} objects.
 */
public final class Requester {
    private Requester() {}

    public static <T> Request<T> emit(T value) {
        return new ConstantRequest<>(value);
    }

    public static <T> Request<T> call(Callable<T> callable) {
        return new CallableRequest<>(callable);
    }
}
