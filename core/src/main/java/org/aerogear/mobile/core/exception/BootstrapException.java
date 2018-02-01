package org.aerogear.mobile.core.exception;

/**
 * This is an unchecked exception that is thrown when bootstrapping a module fails.
 */
public class BootstrapException extends RuntimeException {

    public BootstrapException(String message) {
        super(message);
    }

    public BootstrapException(String message, Throwable rootException) {
        super(message, rootException);
    }

}
