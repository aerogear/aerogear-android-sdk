package org.aerogear.mobile.core.reactive;

import java.util.concurrent.ExecutorService;

/**
 * This interface describes an implementation of the Single Observable Reactive pattern.
 * http://reactivex.io/documentation/single.html
 */
public interface Request<T> {

    /**
     * This configures a handler that will process responses from a request. The underlying request
     * object will begin processing its request when this method is invoked. You may cancel the
     * request with {@link #cancel()}, or you may disconnect the responder with
     * {@link #disconnect(Responder)}. Unless {@link #respondOn(ExecutorService)} is provided, the
     * response will be run on the same thread as the request.
     *
     * @param responder a responder
     * @return a chainable instance of Request, not guaranteed to be the this reference
     */
    Request<T> respondWith(Responder<T> responder);

    /**
     * Requests can be run off the calling thread. This method configures that.
     *
     * @param executorService service to Run the thread on
     * @return a chainable instance of Request, not guaranteed to be the this reference
     */
    Request<T> requestOn(ExecutorService executorService);

    /**
     * Requests may be asynchronous and need to be cancelled. By default this method will interupt
     * the thread the request is running on, but this behavior is overridden with
     * {@link #cancelWith(Canceller)}.
     *
     * Please note, when you cancel a request responders are not called.
     */
    void cancel();

    /**
     * Requests may be asynchronous and need to be cancelled.
     *
     * @param canceller a function to handel cancelling a call
     * @return a chainable instance of Request, not guaranteed to be the this reference
     */
    Request<T> cancelWith(Canceller canceller);

    /**
     * This tells the Request chain to cache its value and not to rerun any underlying generating
     * function.
     *
     * @return a chainable instance of Request, not guaranteed to be the this reference
     */
    Request<T> cache();

    /**
     * Remove a responder from the request. If the request has already been run for this responder
     * then we will do nothing.
     *
     * @param responderToDisconnect The responder to be disconnected
     * @return a request instance that represents the request the parameter was disconnected from.
     */
    Request<T> disconnect(Responder<T> responderToDisconnect);


    /**
     * Responses can be run off the calling thread. This method configures that.
     *
     * @param executorService service to Run the thread on
     * @return a chainable instance of Request, not guaranteed to be the this reference
     */
    Request<T> respondOn(ExecutorService executorService);

    /**
     * This method will apply a transformation to a request using the request thread before it is
     * passed to the request. This function will be run 1:1 with responders in its branch of the
     * train tree.
     *
     * @param mapper a function to transform the result of this request into a new type
     * @param <R> the type to transform into
     * @return a chainable instance of Request, not guaranteed to be the this reference
     */
    <R> Request<R> map(MapFunction<? super T, ? extends R> mapper);
}
