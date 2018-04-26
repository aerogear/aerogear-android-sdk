package org.aerogear.mobile.reactive;


/**
 * This class handles a response from a {@link Request}.
 */
public interface Responder<T> {

    /**
     *
     * This is passed the result of a {@link Request}. Unless the request generated an Exception
     * this will always receive a value. This means that it is the responsibility of the
     * implementation to determine any value of the value.
     *
     * Implementations should not throw exceptions.
     *
     * @param value a result of a request.
     */
    void onResult(T value);

    /**
     * In the event that a {@link Request} throws an exception this method will be invoked.
     *
     * Implementations should not throw exceptions.
     *
     * @param exception an exception that happened during a request.
     */
    void onException(Exception exception);
}
