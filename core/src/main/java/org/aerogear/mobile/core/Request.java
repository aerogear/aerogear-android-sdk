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
     * Requests can be run off the calling thread.  This method configures that.
     *
     * @param executorService service to Run the thread on
     * @return a chainable instance of Request, not guaranteed to be `this`
     */
    Request<T> runOn(ExecutorService executorService);
}
