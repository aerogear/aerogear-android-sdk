package org.aerogear.mobile.core.exception;

/**
 * This is an unchecked exception that is thrown when bootstrapping a module fails.
 */
public class InitializationException extends RuntimeException {

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String message, Throwable rootException) {
        super(message, rootException);
    }

}
