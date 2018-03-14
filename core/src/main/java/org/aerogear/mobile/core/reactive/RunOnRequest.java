package org.aerogear.mobile.core.reactive;

import org.aerogear.mobile.core.Request;
import org.aerogear.mobile.core.Responder;

import java.util.concurrent.ExecutorService;

/**
 * This request will run a Request on a thread provided by RunOn.
 * @param <T>
 */
public class RunOnRequest<T> extends AbstractRequest<T>{

    private final Request<T> delegateTo;
    private final ExecutorService executorService;

    public RunOnRequest(Request<T> delegateTo, ExecutorService executorService) {
        this.delegateTo = delegateTo;
        this.executorService = executorService;
    }

    @Override
    public Request<T> respondWith(final Responder<T> responder) {
        executorService.submit(()->{delegateTo.respondWith(responder);});
        return this;
    }

}
