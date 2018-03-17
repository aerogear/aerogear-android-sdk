package org.aerogear.mobile.core.reactive;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.Request;
import org.aerogear.mobile.core.Responder;
import org.aerogear.mobile.core.utils.SanityCheck;

/**
 * This class performs wrapping and checking for subclasses.
 */
abstract class AbstractRequest<T> implements InternalRequest<T> {

    private final ConcurrentHashMap<Responder<T>, AtomicReference<Responder<T>>> connectedResponders =
                    new ConcurrentHashMap<>();


    @Override
    public final Request<T> requestOn(ExecutorService executorService) {
        nonNull(executorService, "executorService");
        return new RequestOnRequest<>(this, executorService);
    }

    @Override
    public final Request<T> respondOn(ExecutorService executorService) {
        nonNull(executorService, "executorService");
        return new RespondOnRequest<>(this, executorService);
    }

    @Override
    public final Request<T> respondWith(@NonNull Responder<T> responder) {
        SanityCheck.nonNull(responder, "responder");
        connectedResponders.putIfAbsent(responder, new AtomicReference<>(responder));
        return respondWithActual(connectedResponders.get(responder));

    }

    @Override
    public final Request<T> cache() {
        return new CacheRequest<>(this);
    }

    @Override
    public final Request<T> disconnect(Responder<T> responderToDisconnect) {
        AtomicReference<Responder<T>> reference = connectedResponders.remove(responderToDisconnect);

        if (reference != null) {
            reference.set(null);
        }
        return this;
    }

}
