package org.aerogear.mobile.core.reactive;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import android.support.annotation.NonNull;


public class RespondOnRequest<T> extends AbstractRequest<T> {
    private final AbstractRequest<T> delegateTo;
    private final ExecutorService executorService;
    private final AtomicReference<Cleaner> cleanerRef;

    public RespondOnRequest(AbstractRequest<T> delegateTo, ExecutorService executorService) {
        this.delegateTo = delegateTo;
        this.executorService = executorService;
        cleanerRef = new AtomicReference<>(delegateTo.liftCleanupAction());
    }

    @Override
    public void cancel() {
        delegateTo.cancel();

    }

    @Override
    public Request<T> cancelWith(Canceller canceller) {
        delegateTo.cancelWith(canceller);
        return this;
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
                    try {
                        if (originalResponder != null) {
                            originalResponder.onResult(value);
                        }
                    } finally {
                        cleanerRef.get().cleanup();
                    }
                });
            }

            @Override
            public void onException(Exception exception) {
                executorService.submit(() -> {
                    Responder<T> originalResponder = originalResponderRef.get();
                    try {
                        if (originalResponder != null) {
                            originalResponder.onException(exception);
                        }
                    } finally {
                        cleanerRef.get().cleanup();
                    }
                });
            }
        };

        proxiedResponderRef.set(proxiedResponder);
        delegateTo.respondWithActual(proxiedResponderRef);

        return this;
    }

    @Override
    protected Cleaner liftCleanupAction() {
        return cleanerRef.getAndSet(() -> {
        });
    }
}
