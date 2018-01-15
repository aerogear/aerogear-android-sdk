package org.aerogear.mobile.core;

/**
 * This is an unchecked exception that is thrown when bootstrapping a module fails.
 */
class BootstrapException extends RuntimeException {
    public BootstrapException(String message, Throwable rootException) {
        super(message, rootException);
    }

    public BootstrapException(String message) {
        super(message);
    }
}
