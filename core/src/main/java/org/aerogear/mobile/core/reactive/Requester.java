package org.aerogear.mobile.core.reactive;

import org.aerogear.mobile.core.Request;

/**
 * Factory class for creating {@link org.aerogear.mobile.core.Request} objects.
 */
public final class Requester {
    private Requester() {}

    public static <T> Request<T> emit(T value) {
        return new ConstantRequest<T>(value);
    }

}
