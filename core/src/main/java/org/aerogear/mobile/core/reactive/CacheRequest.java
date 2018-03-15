package org.aerogear.mobile.core.reactive;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.aerogear.mobile.core.Request;
import org.aerogear.mobile.core.Responder;

/**
 * This class wraps a request and subscribes itself.  When the request returns a value this request
 * will save the value and emit it to any responders that attach themselves.
 * @param <T> The result type of the underlying request
 */
public final class CacheRequest<T> extends AbstractRequest<T> implements Responder<T> {
    private final Request<T> delegateTo;

    private T cachedResult;
    private Exception cachedException;

    private List<AtomicReference<Responder<T>>> awaitingResponders = Collections.synchronizedList(new ArrayList<>());

    public CacheRequest(Request<T> delegateTo) {
        nonNull(delegateTo, "delegateTo");
        this.delegateTo = delegateTo;
    }

    @Override
    public Request<T> respondWithActual(AtomicReference<Responder<T>> responderRef) {
        Responder<T> responder = responderRef.get();

        if ( responder == null ) {//responder was disconnected, short circuit.
            return this;
        }

        if (cachedResult != null) {// We have a value, short circuit calculating
            responder.onResult(cachedResult);
        } else if (cachedException != null) {// There was an error, short circuit
            responder.onException(cachedException);
        } else {// We need to wait for the value to calculate and send out the result
            awaitingResponders.add(responderRef);
            delegateTo.respondWith(this);
        }
        return this;
    }

    @Override
    public void cancel() {
        delegateTo.cancel();
    }

    @Override
    public void onResult(T value) {
        cachedResult = value;
        for (AtomicReference<Responder<T>> responderRef : awaitingResponders) {
            Responder<T> responder = responderRef.get();
            if (responder != null) {
                responder.onResult(cachedResult);
            }
        }
    }

    @Override
    public void onException(Exception e) {
        cachedException = e;
        for (AtomicReference<Responder<T>> responderRef : awaitingResponders) {
            Responder<T> responder = responderRef.get();
            if (responder != null) {
                responder.onException(cachedException);
            }
        }
    }
}
