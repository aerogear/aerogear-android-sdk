package org.aerogear.mobile.core.http;


/**
 * Generic interface for responses from HTTP Services.  Concrete classes will have the handle Android's
 * threading mechanisms.
 */

public interface HttpResponse {

    /**
     * Creates a callback to be called when the response is finished successfully.
     * <p>
     * The response is delivered immediately if the request has already been finished.
     *
     * @param runnable a callback method
     * @return the instance of the HttpResponse for API chaining
     */
    HttpResponse onComplete(Runnable runnable);

    /**
     * Returns the HTTP status code of the response.
     *
     * @return the status code.
     */
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

    /**
     * Returns true if the requests failed. Use getStatus to identify
     * the reason
     *
     * @return true if request failed
     */
    boolean requestFailed();

    /**
     * Returns the request error if it failed
     *
     * @return Exception request error or null
     */
    Exception getRequestError();
}
