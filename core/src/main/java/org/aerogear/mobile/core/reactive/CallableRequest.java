package org.aerogear.mobile.core.reactive;

import org.aerogear.mobile.core.Request;
import org.aerogear.mobile.core.Responder;
import org.aerogear.mobile.core.utils.SanityCheck;

import java.util.concurrent.Callable;

public class CallableRequest<T> extends AbstractRequest<T> {
    private final Callable<T> callable;

    public CallableRequest(Callable<T> callable) {
        SanityCheck.nonNull(callable, "callable");
        this.callable = callable;
    }

    @Override
    public Request<T> respondWith(Responder<T> responder) {
        SanityCheck.nonNull(responder, "responder");
        try {
            responder.onResult(callable.call());
        } catch (Exception e) {
            responder.onException(e);
        }
        return this;
    }
}
