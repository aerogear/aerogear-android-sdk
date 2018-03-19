package org.aerogear.mobile.core.reactive;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import android.support.annotation.NonNull;

/**
 * This request will run a Request on a thread provided by RunOn.
 *
 * @param <T> type of the value to be emitted
 */
public final class RequestOnRequest<T> extends AbstractRequest<T> {

    private final AbstractRequest<T> delegateTo;
    private final ExecutorService executorService;

    RequestOnRequest(AbstractRequest<T> delegateTo, ExecutorService executorService) {
        this.delegateTo = delegateTo;
        this.executorService = executorService;
    }

    @Override
    public Request<T> respondWithActual(@NonNull final AtomicReference<Responder<T>> responderRef) {
        executorService.submit(() -> delegateTo.respondWithActual(responderRef));
        return this;
    }

    @Override
    public void cancel() {
        delegateTo.cancel();
    }

}
