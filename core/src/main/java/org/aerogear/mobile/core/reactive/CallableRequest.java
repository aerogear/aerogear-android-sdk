package org.aerogear.mobile.core.reactive;

import org.aerogear.mobile.core.Request;
import org.aerogear.mobile.core.Responder;
import org.aerogear.mobile.core.utils.SanityCheck;

import java.util.concurrent.Callable;

/**
 * This class will synchronously invoke a callable parameter when {@link CallableRequest#respondWith(Responder)}
 * is invoked
 *
 * @param <T> a type that this request generates
 */
public class CallableRequest<T> extends AbstractRequest<T> {
    private final Callable<T> callable;
    private Thread callableThread = null;

    public CallableRequest(Callable<T> callable) {
        SanityCheck.nonNull(callable, "callable");
        this.callable = callable;
    }

    @Override
    public Request<T> respondWith(Responder<T> responder) {
        SanityCheck.nonNull(responder, "responder");
        try {
                callableThread = Thread.currentThread();
                responder.onResult(callable.call());
            synchronized (callable) { //We are synchronizing on the callable because we don't want
                                      // the thread reference to go null in the cancel method.
                callableThread = null;
            }
        } catch (Exception e) {
            responder.onException(e);
        }
        return this;
    }

    @Override
    public void cancel() {
        synchronized (callable) {
            if (callableThread != null) {
                callableThread.interrupt();
            }
        }
    }
}
