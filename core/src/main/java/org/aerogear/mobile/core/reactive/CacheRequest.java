package org.aerogear.mobile.core.reactive;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aerogear.mobile.core.Request;
import org.aerogear.mobile.core.Responder;

public class CacheRequest<T> extends AbstractRequest<T> implements Responder<T> {
    private final Request<T> delegateTo;

    private T cachedResult;
    private Exception cachedException;

    private List<Responder<T>> awaitingResponders = Collections.synchronizedList(new ArrayList<>());

    public CacheRequest(Request<T> delegateTo) {
        nonNull(delegateTo, "delegateTo");
        this.delegateTo = delegateTo;
    }

    @Override
    public Request<T> respondWith(Responder<T> responder) {
        if (cachedResult != null) {// We have a value, short circuit calculating
            responder.onResult(cachedResult);
        } else if (cachedException != null) {// There was an error, short circuit
            responder.onException(cachedException);
        } else {// We need to wait for the value to calculate and send out the result
            awaitingResponders.add(responder);
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
        for (Responder<T> responder : awaitingResponders) {
            responder.onResult(cachedResult);
        }
    }

    @Override
    public void onException(Exception e) {
        cachedException = e;
        for (Responder<T> responder : awaitingResponders) {
            responder.onException(cachedException);
        }
    }
}
