package org.aerogear.mobile.core.http;

/**
 * Custom exception thrown when a http request returns an unenxpected
 * result or result code
 */
public class InvalidResponseException extends RuntimeException {
    private final int statusCode;

    public InvalidResponseException(int statusCode) {
        super("Unexpected response from server. Status code was: " + statusCode);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
