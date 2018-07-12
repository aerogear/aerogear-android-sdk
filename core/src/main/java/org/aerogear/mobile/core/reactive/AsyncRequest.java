package org.aerogear.mobile.core.reactive;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


/**
 * This class represents a request to code that delivers its results via an callback.
 *
 * Calls made to the request happen on the requestOn executor
 * The callback is executed on the requestOnExecutor
 * The responded passed to the call back passes its value to a responder defined with respondWith and
 * on the respondOn executor.
 *
 * @param <T> the type emitted from the underlying call
 */
class AsyncRequest<T> extends AbstractRequest<T> {
    public <T> AsyncRequest(Consumer<Responder<T>> callable) {
    }

    @Override
    Request<T> respondWithActual(AtomicReference<Responder<T>> responderRef) {
        return null;
    }

    @Override
    protected Cleaner liftCleanupAction() {
        return null;
    }

    @Override
    public void cancel() {

    }

    @Override
    public Request<T> cancelWith(Canceller canceller) {
        return null;
    }
}
