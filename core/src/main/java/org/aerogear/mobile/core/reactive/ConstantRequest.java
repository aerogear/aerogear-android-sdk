package org.aerogear.mobile.core.reactive;

import org.aerogear.mobile.core.Request;
import org.aerogear.mobile.core.Responder;

/**
 * This is a Request which follows the Single Observer pattern and emits a constant value.
 * @param <T> any type
 */
public class ConstantRequest<T> implements Request<T> {
    private final T value;

    public ConstantRequest(T value) {
        this.value = value;
    }

    @Override
    public Request<T> respondWith(Responder<T> responder) {
        responder.onSuccess(value);
        return this;
    }
}
