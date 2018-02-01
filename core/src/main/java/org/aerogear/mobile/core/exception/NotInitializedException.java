package org.aerogear.mobile.core.exception;

/**
 * This is an unchecked exception that is thrown when MobileCore method called before init
 */
public class NotInitializedException extends RuntimeException {

    public NotInitializedException() {
        super();
    }

}
