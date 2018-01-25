package org.aerogear.mobile.core.http;

import java.io.IOException;

/**
 * Generic interface for responses from HTTP Services.  Concrete classes will have the handle Android's
 * threading mechanisms.
 */

public interface HttpResponse {

    /**
     *
     * Creates a callback to be called when the response is finished successfully.
     *
     * The response is delivered immediately if the request has already been finished.
     *
     * @param runnable a callback method
     * @return the instance of the HttpResponse for API chaining
     */
    HttpResponse onComplete(Runnable runnable);

    int getStatus();

    /**
     * This is a terminal method that will block the thread called on until the http request has been
     * processed.
     */
    void waitForCompletionAndClose();

    /**
     * Blocks and then returns the response from the server
     *
     * @return the body as a string.
     */
    String stringBody();
}
