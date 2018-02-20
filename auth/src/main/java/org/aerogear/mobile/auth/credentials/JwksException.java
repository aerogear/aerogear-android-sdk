package org.aerogear.mobile.auth.credentials;

public class JwksException extends Exception {

    public JwksException(final Throwable error) {
        super(error);
    }

    public JwksException(final String message) {
        super(message);
    }

}
