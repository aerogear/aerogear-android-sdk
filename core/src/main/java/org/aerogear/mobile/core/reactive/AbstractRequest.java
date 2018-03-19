package org.aerogear.mobile.core.reactive;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import android.support.annotation.NonNull;


/**
 * This class performs wrapping and checking for subclasses.
 */
abstract class AbstractRequest<T> implements Request<T> {

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
        nonNull(responder, "responder");
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

    /**
     * This method is the internal implementation of {@link AbstractRequest#respondWith(Responder)}.
     *
     * The abstract class respondWith manages certain cross cutting concerns like null safety, state
     * management, etc. This method is for the mundane tasks that request implementations will need
     * to deal with.
     *
     * @param responderRef a reference to the responder. This reference may become null if the
     *        responder is disconnected.
     * @return a chainable instance of Request, not guaranteed to be `this`
     */
    abstract Request<T> respondWithActual(@NonNull AtomicReference<Responder<T>> responderRef);

}
