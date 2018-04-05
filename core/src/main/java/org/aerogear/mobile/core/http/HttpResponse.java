package org.aerogear.mobile.core.http;


import java.io.InputStream;

/**
 * Generic interface for responses from HTTP Services. Concrete classes will have the handle
 * Android's threading mechanisms.
 */

public interface HttpResponse {
    /**
     * Returns the HTTP status code of the response.
     *
     * @return the status code.
     */
    int getStatus();

    /**
     * This is a terminal method that will block the thread called on until the http request has
     * been processed.
     */
    void waitForCompletionAndClose();

    /**
     * Blocks and then returns the response from the server
     *
     * @return the body as a string.
     */
    String stringBody();

    /**
     * Nonblocking method that returns a stream to the body of the response.
     *
     * @return the body as a stream.
     */
    InputStream streamBody();

    /**
     * Returns the request error if it failed
     *
     * @return Exception request error or null
     */
    Exception getError();
}
