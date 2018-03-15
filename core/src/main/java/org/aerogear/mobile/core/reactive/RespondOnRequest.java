package org.aerogear.mobile.core.reactive;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.Request;
import org.aerogear.mobile.core.Responder;

public class RespondOnRequest<T> extends AbstractRequest<T> {
    private final InternalRequest<T> delegateTo;
    private final ExecutorService executorService;


    public RespondOnRequest(InternalRequest<T> delegateTo, ExecutorService executorService) {
        this.delegateTo = delegateTo;
        this.executorService = executorService;
    }

    @Override
    public void cancel() {
        delegateTo.cancel();

    }

    @Override
    public Request<T> respondWithActual(
                    @NonNull final AtomicReference<Responder<T>> originalResponderRef) {
        /*
         *
         * We have to invoke respondWithActual on our delegate with a Atomic Reference that contains
         * a new Responder, proxiedResponder, that will invoke the original responder,
         * originalResponder.
         *
         * However proxiedResponder still needs to check and make sure that originalResponder wasn't
         * cancelled.
         *
         */
        AtomicReference<Responder<T>> proxiedResponderRef = new AtomicReference<>();
        Responder<T> proxiedResponder = new Responder<T>() {
            @Override
            public void onResult(T value) {
                executorService.submit(() -> {
                    Responder<T> originalResponder = originalResponderRef.get();
                    if (originalResponder != null) {
                        originalResponder.onResult(value);
                    }
                });
            }

            @Override
            public void onException(Exception exception) {
                executorService.submit(() -> {
                    Responder<T> originalResponder = originalResponderRef.get();
                    if (originalResponder != null) {
                        originalResponder.onException(exception);
                    }
                });
            }
        };

        proxiedResponderRef.set(proxiedResponder);
        delegateTo.respondWithActual(proxiedResponderRef);

        return this;
    }
}
