package org.aerogear.mobile.core;

import java.util.concurrent.ExecutorService;

/**
 * This interface describes an implementation of the Single Observable Reactive pattern.
 * http://reactivex.io/documentation/single.html
 */
public interface Request<T> {


    /**
     * This configures a handler that will process responses from a request.
     *
     * @param responder a responder
     * @return a chainable instance of Request, not guaranteed to be `this`
     */
    Request<T> respondWith(Responder<T> responder);

    /**
     * Requests can be run off the calling thread. This method configures that.
     *
     * @param executorService service to Run the thread on
     * @return a chainable instance of Request, not guaranteed to be `this`
     */
    Request<T> runOn(ExecutorService executorService);

    /**
     * Requests may be asynchronous and need to be cancelled.
     */
    void cancel();

    /**
     * This tells the Request chain to cache its value and not to rerun any underlying generating
     * function.
     *
     * @return a chainable instance of Request, not guaranteed to be `this`
     */
    Request<T> cache();

    /**
     * Remove a responder from the request.  If the request has already been run for this responder
     * then we will do nothing.
     *
     * @param responderToDisconnect The responder to be disconnected
     * @return a request instance that represents the request the parameter was disconnected from.
     */
    Request<T> disconnect(Responder<T> responderToDisconnect);
}
