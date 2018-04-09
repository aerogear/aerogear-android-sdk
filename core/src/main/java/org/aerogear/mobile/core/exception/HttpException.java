package org.aerogear.mobile.core.exception;

/**
 * Thrown when an HTTP error happens such as an HTTP response with status 40x or 50x.
 * <p>
 * Connection problems (hostname resolution, timeout, etc.) don't fall into this category. They're
 * essentially IOExceptions.
 */
public class HttpException extends RuntimeException {
    private final int statusCode;

    public HttpException(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Get HTTP status code for this exception.
     *
     * @return HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }
}
